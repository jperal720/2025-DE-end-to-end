while [ $(curl -s -o /dev/null -w %{http_code} http://snowflake-sink-connector:8084/connectors) -ne 200 ]; do
    echo "Waiting for the connector to be ready..."
    sleep 5
done

sw_sink_connector_name=$(jq -r '.name' /connector-config/connector-config.json)
connector_exists=$(curl -s "http://snowflake-sink-connector:8084/$sw_sink_connector_name" | jq -r '.error_code')

if [ $connector_exists == "404" ]; then
    # Initializing snowflake connector's configurations
    curl -X POST -H "Content-Type: application/json" \
        --data @/connector-config/connector-config.json http://snowflake-sink-connector:8084/connectors
else
    echo "Connector $sw_sink_connector_name already exists!"
fi