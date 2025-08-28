# CDC Postgres Source Connector with Debezium

### Build:
In this service we establish a Kafka Connector (using the debezium/connect image). We install 

### Postgres Configurations:

The configurations of our connector regarding the postgres end can be found in the [connector-config.json](./connector-config/connector-config.json).

### Kafka and Schema Registry Configurations:

The envrionment variables set for the kafka configuration can be found on the ~/docker-compose.yaml under the debezium service.

Note: Since we used Avro converters for both keys and value payloads of the logs, we had to setup a series of dependencies on the custom build [see here for more details](https://debezium.io/documentation/reference/stable/configuration/avro.html).

### init-config.sh

The init-config.sh script found ./connector-config sets up the postgres configurations in our Kafka Connect (debezium) service. 
