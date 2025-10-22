package com.investment.users.utils;

/**
 * Utility holder for application-wide constants used across the Users microservice.
 *
 * <p>This class centralizes literal values such as API paths, messages and simple numeric
 * constants to avoid scattering magic values across the codebase. The class is declared
 * final and provides only public static final fields. It is not intended to be instantiated
 * or extended.</p>
 *
 * <p>Example usage:
 * <pre>
 *     String path = Constants.USERS_BASE_PATH;
 * </pre>
 * </p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 21, 2025
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
     * @since October 21, 2025
     */
    private Constants() {
        throw new UnsupportedOperationException(MESSAGE_ERROR_UTILITY);
    }

    public static final String MICROSERVICE_NAME = "users-ms";
    public static final String ENTITY_SCHEMA_TABLE_NAME = "users";
    public static final String ENTITY_INDEX_NAME = "ux_users_email";
    public static final String SECURITY_SCHEME_NAME = "bearer-jwt";
    public static final String VERSION_ONE = "v1";
    public static final String OPEN_API_TITLE_INFO = "Users MS API";
    public static final String OPEN_API_DESCRIPTION_INFO = "Operations for users and admin";
    public static final String BEARER = "bearer";
    public static final String JWT_FORMAT = "JWT";
    public static final String USERS_GROUP = "users";
    public static final String USERS_BASE_PATH = "/api/v1/users";
    public static final String USERS_URI_LOCATION = "/api/users/";
    public static final String OPEN_API_USERS_PATH = "/api/v1/users/**";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String ADMIN_GROUP = "admin";
    public static final String ADMIN_BASE_PATH = "/api/v1/admin";
    public static final String ADMIN_URI_LOCATION = "/api/admin/users/";
    public static final String OPEN_API_ADMIN_PATH = "/api/v1/admin/**";
    public static final String API_DOCS_VERSION_THREE = "/v3/api-docs/**";
    public static final String SWAGGER_ONE = "/swagger-ui.html";
    public static final String SWAGGER_TWO = "/swagger-ui/**";
    public static final String HEALTH_CHECK_PATH = "/actuator/health/**";
    public static final String HEALTH_QUERY = "select 1";
    public static final String DATABASE = "db";
    public static final String OKAY = "ok";
    public static final String FAIL = "fail";
    public static final String APPLICATION_JSON = "application/json";
    public static final String STR_ZERO = "0";
    public static final String STR_TEN = "10";
    public static final String DATE_FROM = "from";
    public static final String DATE_TO = "to";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String USER_ID = "user_id";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String STATUS = "status";
    public static final String UUID = "uuid";
    public static final String TEXT = "citext";
    public static final String STRING_EMPTY_SPACE = " ";
    public static final String STRING_EMPTY = "";
    public static final String CREATED_AT_ATTRIBUTE = "createdAt";

    //Numbers
    public static final int ZERO = 0;
    public static final int SIXTEEN = 16;
    public static final int HUNDRED = 100;

    //Exceptions
    public static final String MESSAGE_NOT_FOUND = "User not found";
    public static final String MESSAGE_BAD_REQUEST = "Bad Request";
    public static final String MESSAGE_UNAUTHORIZED = "Unauthorized";
    public static final String MESSAGE_FORBIDDEN = "Forbidden";
    public static final String MESSAGE_BUSINESS_RULE = "Business Rule";
    public static final String MESSAGE_VALIDATION_ERROR = "Validation error";
    public static final String MESSAGE_CONSTRAINT_VALIDATION = "Constraint violation";
    public static final String MESSAGE_JSON_ERROR = "Malformed JSON request";
    public static final String DATA_INTEGRITY_ERROR = "Data Integrity";
    public static final String MESSAGE_KEY_CONSTRAINT = "Unique/foreign key constraint violated";
    public static final String MESSAGE_INTERNAL_ERROR = "Internal Error";
    public static final String MESSAGE_UNEXPECTED_ERROR = "Unexpected error";
    public static final String FROM_TO_REQUIRED = "'from/to' required";
    public static final String FROM_AFTER_ERROR = "'from' must be <= 'to'";
    public static final String MESSAGE_ERROR_SIZE = "size must be > 0";
    public static final String MESSAGE_ERROR_UTILITY = "Utility class - cannot be instantiated";
}