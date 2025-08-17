import os
import sys
import subprocess

BOOTSTRAP_SERVERS = "broker-1:19092,broker-2:19092,broker-3:19092"

# Topics to be created
TOPICS = [
	"user.info",
	"user.info.cdc.configs",
	"user.info.cdc.offset",
]

def main():
	input_data = sys.stdin.read()
	list = [topic for topic in input_data.split('\n') if topic]

	for topic in TOPICS:
		# Checking if topic exists in broker
		if topic not in list:
			# Generating topic
			shell_result = subprocess.run(
				["./kafka-topics.sh", "--create", "--topic", topic, "--bootstrap-server",
					BOOTSTRAP_SERVERS],
				capture_output=True,
				text=True,
				shell=False
			)

			print("[{topic}] kafka-topics.sh: ", shell_result)

	
	subprocess.run(
		["echo", "Topics in brokers:", BOOTSTRAP_SERVERS],
		capture_output=False
	)
	list_result = subprocess.run(
		["./kafka-topics.sh", "--list", "--bootstrap-server", BOOTSTRAP_SERVERS],
		capture_output=False,
	)



if __name__ == "__main__":
	main()	
