package signup;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;

import java.io.IOException;
import java.util.Properties;

public class UserInfoServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;
        KafkaProducer<String, UserInfoOuterClass.User> kafkaProducer = createKafkaProducer();
        Server server = ServerBuilder.forPort(port)
                .addService(new UserInfoImpl(kafkaProducer))
                .build();
        server.start();
        System.out.println("User-info server started [listening on " + port + "].");
        //Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down User-info (gRPC) server.");
            if (server != null)
                server.shutdown();
            System.err.println("User-info [at port " + port + "] has been shut down.");
        }));
        server.awaitTermination();
    }

    private static KafkaProducer<String, UserInfoOuterClass.User> createKafkaProducer(){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class);
        props.put(KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://0.0.0.0:8081");
        props.put("acks", "all");

        return new KafkaProducer<>(props);
    }
}
