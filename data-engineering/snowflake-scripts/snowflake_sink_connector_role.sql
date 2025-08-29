CREATE DATABASE DEV_KAFKA;

USE DEV_KAFKA;

USE ROLE securityadmin;

CREATE ROLE kafka_connector_user_info;

GRANT USAGE ON DATABASE DEV_KAFKA TO ROLE kafka_connector_user_info;

-- Schema Privileges
GRANT USAGE ON SCHEMA PUBLIC TO ROLE kafka_connector_user_info;
GRANT CREATE TABLE ON SCHEMA PUBLIC TO ROLE kafka_connector_user_info;
GRANT CREATE STAGE ON SCHEMA PUBLIC TO ROLE kafka_connector_user_info;
GRANT CREATE PIPE ON SCHEMA PUBLIC TO ROLE kafka_connector_user_info;

GRANT ROLE kafka_connector_user_info TO USER terupuki;

-- Setting the role as the custom role for the user
ALTER USER terupuki SET DEFAULT_ROLE = kafka_connector_user_info;

DESC USER TERUPUKI;