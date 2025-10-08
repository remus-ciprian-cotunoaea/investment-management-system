package com.investment.accounts.repository;

import com.investment.accounts.entity.CashMovementEntity;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CashMovementRepository extends JpaRepository<CashMovementEntity, UUID> {

    @Query("""
         select c
         from CashMovementEntity c
         where c.accountId = :accountId
         """)
    Page<CashMovementEntity> findAllByAccountId(@Param("accountId") UUID accountId, Pageable pageable);

    @Query("""
         select coalesce(sum(c.amount), 0)
         from CashMovementEntity c
         where c.accountId = :accountId
         """)
    BigDecimal sumAmountByAccountId(@Param("accountId") UUID accountId);
}