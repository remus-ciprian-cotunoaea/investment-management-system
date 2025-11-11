package com.investment.orders.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investment.orders.utils.Constants;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;


/**
 * Kafka configuration for the orders' microservice.
 *
 * <p>This configuration declares Kafka topics and Spring beans for producing and consuming
 * messages as String payloads (JSON encoded). It provides:
 * - topic creation beans for application topics,
 * - a ProducerFactory and KafkaTemplate for sending messages,
 * - an ObjectMapper for JSON handling,
 * - a ConsumerFactory and a ConcurrentKafkaListenerContainerFactory for message listeners.</p>
 *
 * <p>Properties are injected using {@code @Value} (topic names, partition and replication settings)
 * and Spring Boot's {@link KafkaProperties}
 * for producer/consumer base configuration.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Configuration
public class KafkaConfig {

    // ---- Topics (C3)

    /**
     * Creates the 'orders created' topic.
     *
     * @param name       the topic name injected from a configuration property
     * @param partitions number of partitions for the topic
     * @param replication replication factor for the topic
     * @return a {@link NewTopic} instance used by Kafka broker admin to create the topic
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    public NewTopic ordersCreatedTopic(
            @Value(Constants.KAFKA_ORDERS_CREATED) String name,
            @Value(Constants.TOPICS_PARTITIONS) int partitions,
            @Value(Constants.TOPICS_REPLICATION) short replication) {
        return new NewTopic(name, partitions, replication);
    }

    /**
     * Creates the 'trades executed' topic.
     *
     * @param name       the topic name injected from a configuration property
     * @param partitions number of partitions for the topic
     * @param replication replication factor for the topic
     * @return a {@link NewTopic} instance used by Kafka broker admin to create the topic
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    public NewTopic tradesExecutedTopic(
            @Value(Constants.KAFKA_TRADES_EXECUTED) String name,
            @Value(Constants.TOPICS_PARTITIONS) int partitions,
            @Value(Constants.TOPICS_REPLICATION) short replication) {
        return new NewTopic(name, partitions, replication);
    }

    // ---- Producer (String -> String JSON)

    /**
     * Builds a {@link ProducerFactory} configured to serialize both keys and values as Strings.
     *
     * <p>The method starts from Spring Boot's {@link KafkaProperties} and overrides the
     * key/value serializer settings to use {@link StringSerializer}.</p>
     *
     * @param props base Kafka properties provided by Spring Boot
     * @return a configured {@link ProducerFactory} for sending String messages
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    public ProducerFactory<String, String> producerFactory(KafkaProperties props) {
        Map<String, Object> cfg = new HashMap<>(props.buildProducerProperties());
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(cfg);
    }

    /**
     * Creates a {@link KafkaTemplate} backed by the provided {@link ProducerFactory}.
     *
     * <p>Use this template to send messages to Kafka topics from application services.</p>
     *
     * @param pf the producer factory used to create Kafka producers
     * @return a {@link KafkaTemplate} for sending String messages
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
        return new KafkaTemplate<>(pf);
    }

    /**
     * Provides a Jackson {@link com.fasterxml.jackson.databind.ObjectMapper} bean.
     *
     * <p>This mapper can be injected where JSON (de)serialization is required.</p>
     *
     * @return a plain {@link ObjectMapper} instance
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    // ---- Consumer (trades.trade-executed)

    /**
     * Builds a {@link ConsumerFactory} configured to deserialize both keys and values as Strings.
     *
     * <p>The method starts from Spring Boot's {@link KafkaProperties} and overrides the
     * key/value deserializer settings to use {@link org.apache.kafka.common.serialization.StringDeserializer}.</p>
     *
     * @param props base Kafka properties provided by Spring Boot
     * @return a configured {@link ConsumerFactory} for consuming String messages
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties props) {
        Map<String, Object> cfg = new HashMap<>(props.buildConsumerProperties());
        cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(cfg);
    }

    /**
     * Creates a {@link ConcurrentKafkaListenerContainerFactory} wired with the provided consumer factory.
     *
     * <p>Use this factory for creating Kafka listener containers that handle incoming String messages.</p>
     *
     * @param cf the consumer factory to be used by listener containers
     * @return a configured {@link ConcurrentKafkaListenerContainerFactory}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> cf) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }
}