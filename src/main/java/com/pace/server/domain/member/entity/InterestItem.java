package com.pace.server.domain.member.entity;

import com.pace.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interest_item", indexes = {
        @Index(name = "idx_interest_user", columnList = "user_id, type")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InterestItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterestType type;

    @Column(name = "item_value", nullable = false, length = 100)
    private String value;

    public enum InterestType {
        KEYWORD, // 뉴스 키워드
        STOCK, // 주식 종목코드
        CRYPTO // 코인
    }
}
