package com.investment.portfolios.utils.enums;

/**
 * Enumeration representing the lifecycle status of a portfolio.
 *
 * <p>Possible values:
 * <ul>
 *   <li>ACTIVE - the portfolio is active and available for normal operations.</li>
 *   <li>INACTIVE - the portfolio is temporarily disabled or suspended.</li>
 *   <li>ARCHIVED - the portfolio is archived and should be treated as read-only.</li>
 * </ul>
 * </p>
 *
 * <p>This enum is used across the portfolios microservice to filter and control
 * behavior based on a portfolio's lifecycle state.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
public enum PortfolioStatusEnum {
    ACTIVE,
    INACTIVE,
    ARCHIVED
}
