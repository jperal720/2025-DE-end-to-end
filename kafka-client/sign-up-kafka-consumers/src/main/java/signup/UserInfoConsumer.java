package signup;

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;
import org.apache.kafka.clients.consumer.*;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class UserInfoConsumer {

    private static final String DB = "tiktok-dev";
    private static final String DB_TABLE = "user_info";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/"+DB;
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "password";
    private static final String INSERT_QUERY =
            "INSERT INTO " +  DB_TABLE + " (user_id, mobile_number, email, username, first_name, last_name, user_password) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";


    public static void main(String[] args) throws IOException, InterruptedException, KafkaException {
        try{
//        Creating Consumer
            KafkaConsumer<String, UserInfoOuterClass.User> consumer = createKafkaConsumer();
            consumer.subscribe(Collections.singleton("user.info"));

//        Connecting with postgres client
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);


            while (true) {
                ConsumerRecords<String, UserInfoOuterClass.User> records = consumer.poll(Duration.ofMillis(100));
                if (!records.isEmpty())
                    batchSendRecords(conn, records);
            }
        } catch (Exception e){
            System.err.println("Kafka consumer has stopped: " + e.toString());
        }

    }

    private static KafkaConsumer<String, UserInfoOuterClass.User> createKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sign-up-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //To determine whether consumer reads "-from-beginning" or "latest"


        props.put(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://0.0.0.0:8081");
        props.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, UserInfoOuterClass.User.class);
        props.put(KafkaProtobufDeserializerConfig.USE_LATEST_VERSION, "false");

        return new KafkaConsumer<>(props);
    }

    private static void batchSendRecords(Connection conn, ConsumerRecords<String, UserInfoOuterClass.User> records) throws SQLException {
        try (PreparedStatement pStatement = conn.prepareStatement(INSERT_QUERY)){
            for (ConsumerRecord<String, UserInfoOuterClass.User> record : records){
                UserInfoOuterClass.User msgLoad = record.value();
                pStatement.setString(1, msgLoad.getUserId());
                pStatement.setString(2, msgLoad.getMobileNumber());
                pStatement.setString(3, msgLoad.getEmail());
                pStatement.setString(4, msgLoad.getUsername());
                pStatement.setString(5, msgLoad.getFirstName());
                pStatement.setString(6, msgLoad.getLastName());
                pStatement.setString(7, msgLoad.getPassword());
                pStatement.addBatch();
            }
            pStatement.executeBatch();
            System.out.println("Batch successfully sent to " + DB + "." + DB_TABLE + ".");
        } catch (Exception e){
            System.err.println("Could not send insert into ");
        }
    }
}
