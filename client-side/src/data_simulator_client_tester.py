import UserInfo_pb2
import UserInfo_pb2_grpc
import grpc

def main():
    with grpc.insecure_channel("localhost:50051") as channel:
        stub = UserInfo_pb2_grpc.UserInfoStub(channel)
        # Test message
        user = UserInfo_pb2.User(userId='1',
                                 mobileNumber='NA',
                                 email='terupuki@gmail.com',
                                 username='terupuki',
                                 firstName='Jonathan',
                                 lastName='Peral',
                                 password='password')
        
        transaction_response = stub.addUser(user)
        if transaction_response.success:
            print(f"Response was successful, and user was pushed to [topic: {transaction_response.kafkaTopic}, partition: {transaction_response.partition}]")
        else:
            print("Not successful!")


if __name__ == "__main__":
    main()



    