# init-kafka-topics.sh
until /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-1:9092 --list; do
  echo 'Kafka not ready, retrying...'
  sleep 2
done

/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka-1:9092 \
  --create \
  --if-not-exists \
  --topic candidate-created \
  --partitions 1 \
  --replication-factor 3 \
  --config retention.ms=1209600000 \
  --config retention.bytes=1073741824

echo 'Topic created successfully.'