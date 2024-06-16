package com.orquestradorservice.orquestradorservice.config.kafka;
import com.orquestradorservice.orquestradorservice.core.enums.Etopics;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private static final Integer PARTITION_COUNT = 1;
    private static final Integer REPLICA_COUNT = 1;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffesetReset;

    @Bean
    public ConsumerFactory<String, String> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerProperties());
    }

    private Map<String, Object> consumerProperties(){
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,autoOffesetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerProperties());
    }

    private Map<String, Object> producerProperties(){
        var props = new HashMap<String, Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory());
    }

    private NewTopic buildTopic(String name){
        return TopicBuilder
                .name(name)
                .replicas(REPLICA_COUNT)
                .partitions(PARTITION_COUNT)
                .build();

    }

    @Bean
    public NewTopic startSagaTopic(){
        return buildTopic(Etopics.START_SAGA.getTopic());
    }

    @Bean
    public NewTopic orchestratorTopic(){
        return buildTopic(Etopics.BASE_ORCHESTRATOR.getTopic());
    }


    @Bean
    public NewTopic finishSucess(){
        return buildTopic(Etopics.FINISH_SUCCESS.getTopic());
    }

    @Bean
    public NewTopic finishFail(){
        return buildTopic(Etopics.FINISH_FAIL.getTopic());
    }

    @Bean
    public NewTopic productValidationSucessTopic(){
        return buildTopic(Etopics.PRODUCT_VALIDATION_SUCCESS.getTopic());
    }


    @Bean
    public NewTopic productValidationFailTopic(){
        return buildTopic(Etopics.PRODUCT_VALIDATION_FAIL.getTopic());
    }


    @Bean
    public NewTopic paymentSucessTopic(){
        return buildTopic(Etopics.PAYMENT_SUCCESS.getTopic());
   }

    @Bean
    public NewTopic paymentFailTopic(){
        return buildTopic(Etopics.PAYMENT_FAIL.getTopic());
    }

    @Bean
    public NewTopic inventorySucessTopic(){
        return buildTopic(Etopics.INVENTORY_SUCCESS.getTopic());
    }


    @Bean
    public NewTopic inventoryFailTopic(){
        return buildTopic(Etopics.INVENTORY_FAIL.getTopic());
    }

    @Bean
    public NewTopic notifyEndingSucessTopic(){
        return buildTopic(Etopics.NOTIFY_ENDING.getTopic());
    }
}
