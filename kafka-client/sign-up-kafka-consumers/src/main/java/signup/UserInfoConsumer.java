package signup;

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;
import org.apache.kafka.clients.consumer.*;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class UserInfoConsumer {
    public static void main(String[] args) throws IOException, InterruptedException {
//        Creating Consumer
        try(KafkaConsumer<String, UserInfoOuterClass.User> consumer = createKafkaConsumer()) {
            consumer.subscribe(Collections.singleton("user-info"));

            while (true) {
                ConsumerRecords<String, UserInfoOuterClass.User> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, UserInfoOuterClass.User> record : records) {
                    UserInfoOuterClass.User msgLoad = record.value();
                    System.out.println("userId: " + msgLoad.getUserId()
                            + " mobileNumber: " + msgLoad.getMobileNumber()
                            + " email: " + msgLoad.getEmail()
                            + " username: " + msgLoad.getUsername()
                            + " firstName: " + msgLoad.getFirstName()
                            + " lastName: " + msgLoad.getLastName()
                            + " password: " + msgLoad.getPassword());
                }
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
}
