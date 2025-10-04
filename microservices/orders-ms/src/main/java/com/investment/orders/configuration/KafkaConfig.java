package com.investment.orders.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Configuration
public class KafkaConfig {

    // ---- Topics (C3)
    @Bean
    public NewTopic ordersCreatedTopic(
            @Value("${kafka.topics.orders-created}") String name,
            @Value("${kafka.topics.partitions:3}") int partitions,
            @Value("${kafka.topics.replication:1}") short replication) {
        return new NewTopic(name, partitions, replication);
    }

    @Bean
    public NewTopic tradesExecutedTopic(
            @Value("${kafka.topics.trades-executed}") String name,
            @Value("${kafka.topics.partitions:3}") int partitions,
            @Value("${kafka.topics.replication:1}") short replication) {
        return new NewTopic(name, partitions, replication);
    }

    // ---- Producer (String -> String JSON)
    @Bean
    public ProducerFactory<String, String> producerFactory(KafkaProperties props) {
        Map<String, Object> cfg = new HashMap<>(props.buildProducerProperties());
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(cfg);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper(); // para serializar DTOs/Models a JSON cuando publiques
    }

    // ---- Consumer (para trades.trade-executed)
    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties props) {
        Map<String, Object> cfg = new HashMap<>(props.buildConsumerProperties());
        cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(cfg);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> cf) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }
}