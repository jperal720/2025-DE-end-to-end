SERVER_HOST=sign-up-producer
SERVER_PORT=50051

echo Trying $SERVER_HOST:$SERVER_PORT...
nc -z $SERVER_HOST $SERVER_PORT

while [ $? == 1 ]; do
    echo Sleeping...
    sleep 5
    echo Retrying $SERVER_HOST:$SERVER_PORT...
    nc -z $SERVER_HOST $SERVER_PORT
done

# Once the connection has been established
echo Running client!
python3 ./src/data_simulator_client_tester.py