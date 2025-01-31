package org.com.stocknote.domain.stock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.global.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "stock")
    private List<MemberStock> memberStocks = new ArrayList<>();

    public Stock(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
