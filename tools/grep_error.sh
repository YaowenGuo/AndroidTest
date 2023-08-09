#!/bin/sh

cat monkey_log.txt | grep "NullPointerException"
cat monkey_log.txt | grep "IllegalStateException"
cat monkey_log.txt | grep "OutOfMemoryError"
cat monkey_log.txt | grep "keyDispatchingTimedOut"
cat monkey_log.txt | grep "Restart System"
cat monkey_log.txt | grep "RuntimeException"
cat monkey_log.txt | grep "StaleDataException"
cat monkey_log.txt | grep "IllegalArgumentException"
cat monkey_log.txt | grep "ReadException"
cat monkey_log.txt | grep "IndexOutOf*"
