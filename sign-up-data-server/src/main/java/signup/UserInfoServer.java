package signup;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.io.IOException;
import java.util.Properties;

public class UserInfoServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;
        KafkaProducer<String, byte[]> kafkaProducer = createKafkaProducer();
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

    private static KafkaProducer<String, byte[]> createKafkaProducer(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:29092,localhost:39092,localhost:49092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("acks", "all");

        return new KafkaProducer<>(props);
    }
}
