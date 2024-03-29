    package com.example.webapp_shop_ecommerce.entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.math.BigDecimal;

    @Entity
    @Table(name = "BillDetails")
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public class BillDetails extends BaseEntity{
        @Column(name = "quantity")
        private Integer quantity;
        @Column(name = "unit_price")
        private BigDecimal unitPrice;

        @Enumerated(EnumType.STRING)
        @Column(name = "status")
        private BillStatus status;

        @ManyToOne
        @JoinColumn(name = "bill_id")
        private Bill bill;
        @ManyToOne
        @JoinColumn(name = "product_detail_id")
        private ProductDetails productDetails;
    }
