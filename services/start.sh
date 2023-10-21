#!/bin/sh
echo '~~~~~~ BUILDING SPRING-BOOT SERVICES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
mvn clean install -DskipTests
echo '~~~~~~ BUILDING DOCKER IMAGES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~ 1. BULDING REST IMAGE ~~~~~~'
docker build ./restapi -t restapi.jar
echo '~~~~~~ 2. BULDING VIBER-BOT IMAGE ~~~~~~'
docker build ./bot -t bot.jar

echo '~~~~~~ CREATING DOCKER NETWORK ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
docker network create nginx-net
echo '~~~~~~ STARTING DOCKER SERVICES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~ 1. STARTING NGINX ~~~~~~'
docker-compose -f ./nginx/docker-compose.yml down
docker-compose -f ./nginx/docker-compose.yml up -d
sleep 15
echo '~~~~~~ 2. STARTING REST ~~~~~~'
docker-compose -f ./rest/docker-compose.yml down
docker-compose -f ./rest/docker-compose.yml up -d
sleep 15
echo '~~~~~~ 3. STARTING VIBER-BOT ~~~~~~'
docker-compose -f ./viber-bot/docker-compose.yml down
docker-compose -f ./viber-bot/docker-compose.yml up -d
sleep 15