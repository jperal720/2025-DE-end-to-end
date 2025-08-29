#!/bin/sh
./kafka-topics.sh --list --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 | python3 ./custom_scripts/check-topics.py