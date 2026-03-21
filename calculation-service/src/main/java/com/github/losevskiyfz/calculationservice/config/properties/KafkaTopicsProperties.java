package com.github.losevskiyfz.calculationservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaTopicsProperties {

    private Map<String, TopicProperties> topics = new HashMap<>();

    public Map<String, TopicProperties> getTopics() { return topics; }
    public void setTopics(Map<String, TopicProperties> topics) { this.topics = topics; }

    public static class TopicProperties {
        private String name;
        private int partitions;
        private int replicas;
        private String retentionMs;
        private String retentionBytes;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPartitions() {
            return partitions;
        }

        public void setPartitions(int partitions) {
            this.partitions = partitions;
        }

        public int getReplicas() {
            return replicas;
        }

        public void setReplicas(int replicas) {
            this.replicas = replicas;
        }

        public String getRetentionMs() {
            return retentionMs;
        }

        public void setRetentionMs(String retentionMs) {
            this.retentionMs = retentionMs;
        }

        public String getRetentionBytes() {
            return retentionBytes;
        }

        public void setRetentionBytes(String retentionBytes) {
            this.retentionBytes = retentionBytes;
        }
    }
}