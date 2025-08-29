import UserInfo_pb2
import UserInfo_pb2_grpc
import grpc
import time

def main():
    with grpc.insecure_channel("sign-up-producer:50051") as channel:
        print("Creating stub...")
        stub = UserInfo_pb2_grpc.UserInfoStub(channel)
        # Test message
        i = 0
        print("Starting user generation...")
        while(True):
            user = UserInfo_pb2.User(userId=f"{i}",
                                    mobileNumber=f"+1 (555)-{i}",
                                    email=f"johndoe{i}@gmail.com",
                                    username=f"johndoe{i}",
                                    firstName="John",
                                    lastName=f"Doe the {i}",
                                    password="password")
            
            transaction_response = stub.addUser(user)
            if transaction_response.success:
                print(f"Response was successful, and user was pushed to [topic: {transaction_response.kafkaTopic}, partition: {transaction_response.partition}]")
            else:
                print("Not successful!")

            i+=1
            # Sleep for one second
            time.sleep(1)


if __name__ == "__main__":
    main()



    