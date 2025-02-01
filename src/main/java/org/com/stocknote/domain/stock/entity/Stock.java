package org.com.stocknote.domain.stock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.global.base.BaseEntity;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Stock {
    @Id
    private String code; // ID를 자동생성에서 직접 코드 사용으로 변경
    private String name;
    private String market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = true) // nullable = true 추가
    private Member member;

    public Stock(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
