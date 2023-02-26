#!/bin/sh
mvn clean install -DskipTests
docker build . -t foodrest.jar
docker-compose down
docker-compose up -d