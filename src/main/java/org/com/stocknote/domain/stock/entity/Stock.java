package org.com.stocknote.domain.stock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.memberStock.entity.MemberStock;

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
    private String code;

    @Column(name = "stock_index")
    private Long stockIndex;  // AUTO_INCREMENT는 DB에서 관리

    private String name;
    private String market;

    @OneToMany(mappedBy = "stock")
    private List<MemberStock> memberStocks = new ArrayList<>();

    public Stock(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
