package com.github.losevskiyfz.candidateservice.config;

import com.github.losevskiyfz.candidateservice.config.properties.KafkaTopicsProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopics kafkaTopics(KafkaTopicsProperties props) {
        NewTopic[] topics = props.getTopics().values().stream()
                .map(t -> TopicBuilder.name(t.getName())
                        .partitions(t.getPartitions())
                        .replicas(t.getReplicas())
                        .config(TopicConfig.RETENTION_MS_CONFIG, t.getRetentionMs())
                        .config(TopicConfig.RETENTION_BYTES_CONFIG, t.getRetentionBytes())
                        .build())
                .toArray(NewTopic[]::new);
        return new NewTopics(topics);
    }
}
