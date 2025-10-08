package com.investment.accounts.entity;

import com.investment.accounts.utils.enums.CashMovementStatusEnum;
import com.investment.accounts.utils.enums.CashMovementTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cash_movements", schema = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashMovementEntity {

    @Id
    @Column(name = "cash_movement_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity accountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id", nullable = false)
    private CurrencyEntity currency;

    @Column(name = "amount", precision = 28, scale = 10, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 24)
    private CashMovementTypeEnum type; // DEPOSIT, WITHDRAWAL, FEE, DIVIDEND, INTEREST, TAX, OTHER

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CashMovementStatusEnum status; // PENDING, COMPLETED, CANCELED, REJECTED

    @Column(name = "date", nullable = false)
    private OffsetDateTime date;

    @Column(name = "note")
    private String note;
}