#!/bin/bash

gradle build
while true
do
    java -jar ./build/libs/challenge-7.jar
done
