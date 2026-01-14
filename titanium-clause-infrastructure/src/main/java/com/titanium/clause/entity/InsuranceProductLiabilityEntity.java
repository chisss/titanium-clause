package com.titanium.clause.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保险产品责任关联实体类
 */
@Entity
@Table(name = "t_insurance_product_liability",
        indexes = {
            @Index(name = "idx_prod_liab_product_id", columnList = "product_id"),
            @Index(name = "idx_prod_liab_liability_id", columnList = "liability_id")
        }
)
@Data
@NoArgsConstructor
public class InsuranceProductLiabilityEntity {
    @EmbeddedId
    private InsuranceProductLiabilityId id;

    @Column(name = "coverage", precision = 18, scale = 2)
    private Double coverage;

    @Column(name = "premium_rate", precision = 18, scale = 6)
    private Double premiumRate;

    /**
     * 复合主键类
     */
    @Embeddable
    @Data
    @NoArgsConstructor
    public static class InsuranceProductLiabilityId {
        @Column(name = "product_id", length = 32, nullable = false)
        private String productId;

        @Column(name = "liability_id", length = 32, nullable = false)
        private String liabilityId;

        public InsuranceProductLiabilityId(String productId, String liabilityId) {
            this.productId = productId;
            this.liabilityId = liabilityId;
        }
    }
}