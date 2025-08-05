package signup;

import io.grpc.Status;
import io.grpc.StatusException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;


public class UserInfoImpl extends UserInfoGrpc.UserInfoImplBase {
    private final KafkaProducer<String, byte[]> kafkaProducer;
    private final String topic = "user-info";

    public UserInfoImpl(KafkaProducer<String, byte[]> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void addUser(UserInfoOuterClass.User request,
                        io.grpc.stub.StreamObserver<UserInfoOuterClass.TransactionResponse> responseObserver) {
        try {
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                    topic,
                    request.getUserId(), //Record key
                    request.toByteArray() //Record load
            );

            kafkaProducer.send(record, (metadata, exception) -> {
                if (exception == null){
                    UserInfoOuterClass.TransactionResponse response = UserInfoOuterClass
                            .TransactionResponse
                            .newBuilder()
                            .setSuccess(true)
                            .setKafkaTopic(metadata.topic())
                            .setPartition(metadata.partition())
                            .build();
                    responseObserver.onNext(response);
                }else {
                    responseObserver.onError(Status.INTERNAL
                            .withDescription("Kafka publish failed: " + exception.getMessage())
                            .asRuntimeException());
                }
                responseObserver.onCompleted();
            });
        } catch (Exception e){
                    responseObserver.onError(Status.INTERNAL
                            .withDescription("Server error: " + e.getMessage())
                            .asRuntimeException());
        }
    }
}
