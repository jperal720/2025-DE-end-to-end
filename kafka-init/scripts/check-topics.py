import os
import sys
import subprocess

BOOTSTRAP_SERVERS = "broker-1:19092,broker-2:19092,broker-3:19092"

def main():
	input_data = sys.stdin.read()
	list = [topic for topic in input_data.split('\n') if topic]

	create_user_info(list)
	create_cdc_postgres_source_topics(list)
	
	subprocess.run(
		["echo", "Topics in brokers:", BOOTSTRAP_SERVERS],
		capture_output=False
	)
	subprocess.run(
		["./kafka-topics.sh", "--list", "--bootstrap-server", BOOTSTRAP_SERVERS],
		capture_output=False,
	)


# GENERATING USER.INFO
def create_user_info(list):
	USER_INFO = 'user.info'
	PARTITIONS = "3"
	REPLICATION_FACTOR = "3"


	if USER_INFO not in list:
		subprocess.run(["echo", "Generating user.info:"], capture_output=False)
		shell_result = subprocess.run(
			["./kafka-topics.sh", "--create", "--topic", USER_INFO,
				"--partitions", PARTITIONS,
				"--replication-factor", REPLICATION_FACTOR,
			 	"--bootstrap-server", BOOTSTRAP_SERVERS
			],
			capture_output=True,
			text=True,
			shell=False
		)
		print(f"[{USER_INFO}] kafka-topics.sh: {shell_result}")

# GENERATING CDC POSTGRES SOURCE CONNECTOR TOPICS
def create_cdc_postgres_source_topics(list):
	TOPICS = [
		"user.info.cdc.configs",
		"user.info.cdc.offset",
		"user.info.cdc.status"
	]
	PARTITIONS = "1"
	REPLICATION_FACTOR = "3"
	CLEANUP = 'cleanup.policy=compact'

	for topic in TOPICS:
		if topic not in list:
			subprocess.run(["echo", f"[{topic}] Generating cdc postgres source connector topics: "], capture_output=False)
			shell_result = subprocess.run(
				["./kafka-topics.sh", "--create", "--topic", topic,
					"--partitions", PARTITIONS,
					"--replication-factor", REPLICATION_FACTOR,
					"--config", CLEANUP,
					"--bootstrap-server", BOOTSTRAP_SERVERS
				],
				capture_output=True,
				text=True,
				shell=False
			)

			print(f"[{topic}] kafka-topics.sh: {shell_result}")

if __name__ == "__main__":
	main()	
