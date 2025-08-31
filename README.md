# 2025-DE-end-to-end Description
Containerized end-to-end data solution for a simulated social-media project. The project is divided into three parts: Data-Engineering end, Machine-Learning end, and Data-Analysis end.


### Date Engineering Pipelines:
- Composed of a data simulator which generates user data, video engagement data, and advertisement data. The data is sent from a client to a gRPC server, using Protobuf serialization the server pushes the data into a kafka topic - schema registry is set up to ensure schema compatibility.
- As a Kafka Client, Apache Flink performs Stateful Stream Processing for the production of more-intricate data that will be useful for data analysis and ML purposes.
- From Kafka:
    - We push our transactional data - e.g. sign-up-user information, user behaviour per session, etc. - to our postgres instance
        - Data sent to postgres is also sent back to Kafka through Debezium CDC Kafka Connectors and into Snowflake (via Snowflake's kafka sink connector)
    - We push our analysis-ready records to Snowflake 
- With Grafana and Prometheus we track metrics such as Kafka latency, throughput, etc. Additionally, we setup fault-tolerance alerts in case a certain part of our datastream pipeline fails.

### Machine Learning Pipeline (Airflow DAG):
- With Apache Airflow we orchestrate batch training jobs –data is extracted from our Snowflake instance– on a weekly basis.
    - Model is then trained
    - Log metrics of our experiments and corresponding model into MLFlow.

### Analytics & Visualization:
- Using Apache Superset we connect to our data warehouse (Snowflake) for SQL Analysis and visualizing trends.

### Streamlit
- We use Streamlit to visualize model performance, and other key metrics.

## System Architecture Visualized
![system_architecture_image](architecture/System%20Architecture.drawio.png)



# To Run Project:

- Clone repository:

    - ```git clone git@github.com:jperal720/2025-DE-end-to-end.git```

- Enter repository:

    - ```cd 2025-DE-end-to-end```

- Snowflake set up:
    - Setup your own Snowflake instance and create a database called "DEV_KAFKA"
    - Follow the instructions in [snowflake-sink-connectors](data-engineering/kafka-clients/workers/snowflake-sink-connectors) to set up the connector-config.json file.

- Run Docker instances:

    - ```docker compose up -d --build```

- To ensure that everything is running properly, check that the process instance is receiving records, and similarly snowflake's tables in DEV_KAFKA.PUBLIC. 
