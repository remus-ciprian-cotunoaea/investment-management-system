package com.investment.accounts.entity;

import com.investment.accounts.utils.enums.AccountStatusEnum;
import com.investment.accounts.utils.enums.AccountTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "accounts",
        schema = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_accounts_broker_accnum_currency",
                        columnNames = {"broker_id", "account_number", "currency_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private UUID id;

    // FK internos reales
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "broker_id", nullable = false)
    private BrokerEntity broker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id", nullable = false)
    private CurrencyEntity currency;

    @Column(name = "portfolio_id", nullable = false)
    private UUID portfolioId;

    @Column(name = "account_number", length = 64, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 16, nullable = false)
    private AccountTypeEnum type;   // CASH, MARGIN, FUTURES, OTHER

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private AccountStatusEnum status; // ACTIVE, INACTIVE, CLOSED, SUSPENDED, ARCHIVED

    @Column(name = "opened_at", nullable = false)
    private OffsetDateTime openedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;
}