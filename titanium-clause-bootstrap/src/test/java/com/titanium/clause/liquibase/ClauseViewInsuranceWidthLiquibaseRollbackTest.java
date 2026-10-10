package com.titanium.clause.liquibase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * 条款读模型险种列宽收敛变更集测试（G10/g10-02）。
 * <p>
 * 该变更集把 {@code t_clause_view.insurance_type} 由 VARCHAR(50) 收窄至 VARCHAR(32)
 * 并补齐语义注释。收窄若写错宽度或漏 rollback 语句不会在 SQL 文本层面暴露，
 * 故在 H2 上验证「列宽收窄生效、存量值不丢、回滚还原原宽度」。
 * </p>
 */
class ClauseViewInsuranceWidthLiquibaseRollbackTest {

    private static final String CHANGELOG =
            "liquibase/ddl/clause_view_insurance_width_202610091849_weisun_ddl.sql";

    @Test
    void shouldNarrowInsuranceTypeWidthAndRollback() throws Exception {
        String databaseName = "clause_" + UUID.randomUUID().toString().replace("-", "");
        String url = "jdbc:h2:mem:" + databaseName + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
        try (Connection connection = DriverManager.getConnection(url, "sa", "")) {
            try (Statement statement = connection.createStatement()) {
                // 变更前形态替身：列宽 50
                statement.execute("CREATE TABLE t_clause_view ("
                        + "clause_id VARCHAR(64) PRIMARY KEY,"
                        + "insurance_type VARCHAR(50) NULL)");
                statement.execute("INSERT INTO t_clause_view VALUES ('CLS-001', 'TERM_LIFE')");
            }
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(
                    CHANGELOG, new ClassLoaderResourceAccessor(), database);

            liquibase.update(new Contexts(), new LabelExpression());
            assertEquals(32, columnWidth(connection, "insurance_type"), "应收窄至 32");
            assertEquals("TERM_LIFE", valueOf(connection, "CLS-001"), "收窄不得丢值");

            liquibase.rollback(1, new Contexts(), new LabelExpression());
            assertEquals(50, columnWidth(connection, "insurance_type"), "回滚应还原 50");
            assertEquals("TERM_LIFE", valueOf(connection, "CLS-001"), "回滚不得丢值");
        }
    }

    private int columnWidth(Connection connection, String columnName) throws Exception {
        String sql = "SELECT CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS"
                + " WHERE LOWER(TABLE_NAME) = 't_clause_view' AND LOWER(COLUMN_NAME) = LOWER(?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, columnName);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    private String valueOf(Connection connection, String clauseId) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT insurance_type FROM t_clause_view WHERE clause_id = ?")) {
            statement.setString(1, clauseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getString(1);
            }
        }
    }
}
