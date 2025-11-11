package com.investment.orders.utils;

public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException(MESSAGE_ERROR_UTILITY);
    }

    public static final String MICROSERVICE_NAME = "orders-ms";
    public static final String VERSION_ONE = "v1";
    public static final String SERVICE = "service";
    public static final String OWNER = "owner";
    public static final String INVESTMENT_PLATFORM = "investment-platform";
    public static final String DESCRIPTION = "description";
    public static final String DESCRIPTION_CONTENT = "Gestión de órdenes y ejecución";
    public static final String KAFKA_ORDERS_CREATED = "${kafka.topics.orders-created}";
    public static final String KAFKA_TRADES_EXECUTED = "${kafka.topics.trades-executed}";
    public static final String TOPICS_PARTITIONS = "${kafka.topics.partitions:3}";
    public static final String TOPICS_REPLICATION = "${kafka.topics.replication:1}";
    public static final String KAFKA_TOPICS_ORDERS_CREATED = "${app.kafka.topics.orderCreated}";
    public static final String KAFKA_TOPICS_TRADES_EXECUTED = "${app.kafka.topics.tradeExecuted}";
    public static final String KAFKA_FAILED_ORDER = "Kafka publish failed for order {}: {}";
    public static final String KAFKA_ORDER_RESPONSE_ID = "OrderResponseDto.id";
    public static final String KAFKA_EXECUTION_RESPONSE_ID = "ExecutionResponseDto.orderId/id";
    public static final String PUBLISHING_ORDER_CREATED = "Publishing order-created | key={} | topic={}";
    public static final String PUBLISHING_TRADE_EXECUTED = "Publishing trade-executed | key={} | topic={}";
    public static final String MIT_LICENSE = "MIT";
    public static final String STATUS = "status";
    public static final String NOT_NULL = " must not be null";
    public static final String OPEN_API_TITLE_INFO = "Orders MS API";
    public static final String OPEN_API_DESCRIPTION_INFO = "REST API of the order microservice";
    public static final String ORDERS_GROUP = "orders";
    public static final String TRADES_GROUP = "trades";
    public static final String ORDER_ID = "order_id";
    public static final String INSTRUMENT_ID = "instrument_id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String TRADE_ID = "trade_id";
    public static final String SIDE = "side";
    public static final String ORDER_TYPE = "order_type";
    public static final String QUANTITY = "quantity";
    public static final String LIMIT_PRICE = "limit_price";
    public static final String PLACED_AT = "placed_at";
    public static final String NOTE = "note";
    public static final String PRICE = "price";
    public static final String FEES = "fees";
    public static final String TAXES = "taxes";
    public static final String EXECUTED = "executedAt";
    public static final String PLACED = "placedAt";
    public static final String EXECUTED_AT = "executed_at";
    public static final String SETTLEMENT_DATE = "settlement_date";
    public static final String ORDERS_BASE_PATH = "/api/v1/orders";
    public static final String EXECUTIONS_BASE_PATH = "/api/v1/executions";
    public static final String APPLICATION_JSON = "application/json";
    public static final String API_DOCS_VERSION_THREE = "/v3/api-docs/**";
    public static final String PATH_ACCOUNT_ID = "?accountId=";
    public static final String SWAGGER_ONE = "/swagger-ui.html";
    public static final String SWAGGER_TWO = "/swagger-ui/**";
    public static final String HEALTH_CHECK_PATH = "/actuator/health";
    public static final String HEALTH_INFO = "/actuator/info";
    public static final String UUID = "uuid";
    public static final String QUANTITY_GREATER_THAN_ZERO = "quantity must be > 0";
    public static final String PRICE_GREATER_THAN_ZERO = "price must be > 0";
    public static final String LIMIT_PRICE_GREATER_THAN_ZERO = "limitPrice must be >= 0 for ";
    public static final String SIZE_GREATER_THAN_ZERO = "size must be > 0";
    public static final String ORDER_NOT_FOUND_ACCOUNT = "order not found for account";
    public static final String ORDER_NOT_FOUND = "order not found";
    public static final String NO_EXECUTION_FOR_ORDER = "no executions for order";
    public static final String PENDING_UPDATE_ONLY = "Only PENDING orders can be updated";
    public static final String SPACE = " ";
    public static final String ZERO = "0";
    public static final String TWENTY = "20";

    //Numbers
    public static final int INT_ZERO = 0;
    public static final int INT_ONE = 1;
    public static final int INT_SIX = 6;
    public static final int INT_TEN = 10;
    public static final int INT_EIGHTEEN = 18;
    public static final int INT_TWENTY_EIGHT = 28;

    //Exceptions
    public static final String MESSAGE_NOT_FOUND = "NotFoundException: {}";
    public static final String MESSAGE_BUSINESS_ERROR = "BusinessException: {}";
    public static final String MESSAGE_VALIDATION_ERROR = "Validation error: {}";
    public static final String MESSAGE_VALIDATION_FAILED = "Validation failed";
    public static final String MESSAGE_CONSTRAINT_VIOLATION = "Constraint violation: {}";
    public static final String MESSAGE_CONSTRAINT = "Constraint violation";
    public static final String MESSAGE_HTTP_NOT_READABLE = "HttpMessageNotReadableException: {}";
    public static final String MESSAGE_MALFORMED_FORMAT = "Malformed request body: ";
    public static final String MESSAGE_ILLEGAL_ARGUMENT = "Illegal argument/state: {}";
    public static final String MESSAGE_UNEXPECTED_ERROR = "Unexpected error";
    public static final String MESSAGE_ERROR_UTILITY = "Utility class - cannot be instantiated";
}