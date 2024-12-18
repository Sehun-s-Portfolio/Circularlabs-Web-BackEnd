package com.web.circularlabs_web_backend.product.domain;

import com.web.circularlabs_web_backend.share.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class ClientProduct extends TimeStamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long clientProductId; // 인덱스

    @Column(nullable = false)
    private String clientClassficationCode; // 구분 코드

    @JoinColumn(name = "supplyProductId")
    @OneToOne
    private SupplyProduct supplyProduct; // 공급사 상품으로 매핑된 상품 id
}
