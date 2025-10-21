package com.investment.users.utils.enums;

/**
 * Enumeration of possible user account statuses used across the application.
 *
 * <p>These statuses represent the lifecycle state of a user and are used for
 * filtering, authorization decisions, display purposes and business rules.</p>
 *
 * <ul>
 *   <li>ACTIVE: the account is enabled and the user can authenticate and act in the system.</li>
 *   <li>INACTIVE: the account is disabled, suspended or otherwise prevented from authenticating.</li>
 * </ul>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
public enum UserStatusEnum {

    ACTIVE,
    INACTIVE
}
