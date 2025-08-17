#!bin/bash

while [ $(curl -s -o /dev/null -w %{http_code} http://debezium:8083/connectors) -ne 200 ]; do
    echo "Waiting for Kafka connect to be ready..."
    sleep 5
done

# Init connector's config
curl -X POST -H "Content-Type: application/json" \
    --data @/kafka/connector-config/connector-config.json http://debezium:8083/connectors