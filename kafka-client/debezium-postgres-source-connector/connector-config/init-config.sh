while [ $(curl -s -o /dev/null -w %{http_code} http://debezium:8083/connectors) -ne 200 ]; do
    echo "Waiting for Kafka connect to be ready..."
    sleep 5
done

dbz_connector_name=$(jq -r '.name' /kafka/connector-config/connector-config.json)
connector_exists=$(curl -s "http://debezium:8083/connector/$dbz_connector_name" | jq -r '.error_code')

if [ $connector_exists == "404" ]; then
    # Init connector's config
    curl -X POST -H "Content-Type: application/json" \
        --data @/kafka/connector-config/connector-config.json http://debezium:8083/connectors
else
    echo "Connector $dbz_connector_name already exists!"

fi