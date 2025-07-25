# 2025-DE-end-to-end
Containerized end-to-end data solution for a simulated social media project. The project is divided into three parts: DE pipeline, ML pipeline, and monitoring.


### Date Engineering Pipeline:
- Composed of a data simulator which generates user data, video engagement data, and advertisement data. The data will be simulated by a gRPC server, which uses Protobuf to ensure schema compatibility.
- Additionally, the data is ingested into an Apache Kafka stream. Schema registry validates schema compatibility between Kafka clients.
- Then, we perform Stateful Stream Processing with Apache Flink.
- Push processed data into Apache Iceberg tables.
- With Grafana and Prometheus we track metrics such as Kafka latency, throughput, etc. Additionally, we setup fault tolerance alerts in case a certain part of our datastream fails.

### Machine Learning Pipeline (Airflow DAG):
- With Apache Airflow we orchestrate batch training jobs –data is extracted from Apache Iceberg– on a weekly basis.
- Model is then trained
- Log metrics of our experiments and corresponding model into MLFlow.

### Analytics & Visualization:
- Using Apache Superset we connect to our Iceberg for SQL Analysis and visualizing trends.
- We use Streamlit to visualize model performance, and other key metrics.
