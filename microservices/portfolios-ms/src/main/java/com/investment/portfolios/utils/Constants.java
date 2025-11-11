package com.investment.portfolios.utils;

/**
 * Centralized application constants for the Portfolios microservice.
 *
 * <p>This final utility class groups string and numeric constants used across the
 * portfolios microservice (for example: API paths, message templates, DB column
 * names and commonly reused numeric values). It is not intended to be instantiated
 * or extended.</p>
 *
 * <p>Usage:
 * - Reference constants statically, e.g. {@code Constants.PORTFOLIOS_BASE_PATH}.
 * - Do not add runtime logic or mutable state here; keep values constant and
 *   descriptive.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 25, 2025
 */
public final class Constants {

        /**
         * Private constructor to prevent instantiation of this utility class.
         *
         * <p>Always throws UnsupportedOperationException to make the intent explicit:
         * this class only provides static constants and should not be instantiated.</p>
         *
         * @throws UnsupportedOperationException always thrown to prevent instantiation
         * @author Remus-Ciprian Cotunoaea
         * @since October 25, 2025
         */
        private Constants() {
            throw new UnsupportedOperationException(MESSAGE_ERROR_UTILITY);
        }

        public static final String MICROSERVICE_NAME = "portfolios-ms";
        public static final String SERVICE = "service";
        public static final String STATUS = "status";
        public static final String RUNNING = "running";
        public static final String OPEN_API_TITLE_INFO = "Portfolios MS API";
        public static final String OPEN_API_DESCRIPTION_INFO = "REST API for Portfolios microservice";
        public static final String PORTFOLIOS_VERSION = "1.0.0-SNAPSHOT";
        public static final String PORTFOLIOS_GROUP = "portfolios";
        public static final String PORTFOLIOS_ID = "id";
        public static final String UNIQUE_CONSTRAINT = "uk_portfolios_user_name";
        public static final String PORTFOLIOS_BASE_PATH = "/api/v1/portfolios";
        public static final String USER_ID = "user_id";
        public static final String PORTFOLIOS_NAME = "name";
        public static final String INDEX_PORTFOLIO_USER = "ix_portfolios_user";
        public static final String API_DOCS_VERSION_THREE = "/v3/api-docs/**";
        public static final String SWAGGER_ONE = "/swagger-ui.html";
        public static final String SWAGGER_TWO = "/swagger-ui/**";
        public static final String HEALTH_CHECK_PATH = "/actuator/health";
        public static final String HEALTH_INFO = "/actuator/info";
        public static final String API = "/api/**";
        public static final String CREATED_AT = "created_at";
        public static final String UUID = "uuid";

        //Numbers
        public static final int TWENTY = 20;
        public static final int HUNDRED = 100;

        //Exceptions
        public static final String MESSAGE_NOT_FOUND = "Portfolio not found";
        public static final String MESSAGE_NOT_FOUND_USER = "Portfolio not found for this user";
        public static final String MESSAGE_BAD_REQUEST = "Bad Request";
        public static final String MESSAGE_INTERNAL_ERROR = "Internal Error";
        public static final String MESSAGE_ERROR_UTILITY = "Utility class - cannot be instantiated";
}
