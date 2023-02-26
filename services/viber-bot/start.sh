#!/bin/sh
mvn clean install -DskipTests
docker build . -t bot.jar
docker-compose down
docker-compose up -d