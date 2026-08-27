package com.titanium.clause.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.springboot.autoconfig.JpaAutoConfiguration;
import org.axonframework.springboot.util.DeadLetterQueueProviderConfigurerModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport.ConditionAndOutcomes;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import com.titanium.clause.ClauseApplication;

@SpringBootTest(classes = ClauseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:clause-dlq;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.liquibase.enabled=false",
                "spring.cloud.openfeign.client.config.ruleEngineApi.url=http://127.0.0.1:1",
                "axon.axonserver.enabled=false"
        })
class ClauseDeadLetterQueueContextTest {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private EventProcessingConfiguration eventProcessingConfiguration;

    @Test
    void reportsJpaAutoConfigurationAndProvidesClauseDeadLetterProcessor() {
        Map<String, DeadLetterQueueProviderConfigurerModule> providers =
                applicationContext.getBeansOfType(DeadLetterQueueProviderConfigurerModule.class);
        ConditionEvaluationReport report = ConditionEvaluationReport
                .get(applicationContext.getBeanFactory());
        ConditionAndOutcomes outcomes = report.getConditionAndOutcomesBySource()
                .get(JpaAutoConfiguration.class.getName());

        assertEquals(1, providers.size());
        assertEquals(Set.of("clauseDeadLetterQueueProviderConfigurerModule"), providers.keySet());
        assertNotNull(outcomes);
        assertFalse(outcomes.isFullMatch());
        assertTrue(eventProcessingConfiguration.sequencedDeadLetterProcessor("clause-query-group").isPresent());
    }
}
