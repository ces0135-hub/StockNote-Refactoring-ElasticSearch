package org.com.stocknote.domain.memberStock.repository;

import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.memberStock.entity.MemberStock;
import org.com.stocknote.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberStockRepository extends JpaRepository<MemberStock, Long> {
    boolean existsByMemberAndStock (Member member, Stock stock);

    List<MemberStock> findByMemberEmailOrderByAddedAtDesc (String email);

    Optional<MemberStock> findByMemberAndStock (Member member, Stock stock);
}
