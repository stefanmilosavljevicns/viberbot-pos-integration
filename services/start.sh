#!/bin/sh
echo '~~~~~~ BUILDING SPRING-BOOT SERVICES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~ 1. BULDING UTIL SERVICE ~~~~~~'
mvn -f ./utils/viber4j clean install -DskipTests
echo '~~~~~~ 2. BULDING REST SERVICE ~~~~~~'
mvn -f ./rest clean install -DskipTests
echo '~~~~~~ 3. BULDING VIBER-BOT SERVICE ~~~~~~'
mvn -f ./viber-bot clean install -DskipTests
echo '~~~~~~ BUILDING DOCKER IMAGES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~ 1. BULDING REST IMAGE ~~~~~~'
docker build ./rest -t foodrest.jar
echo '~~~~~~ 2. BULDING VIBER-BOT IMAGE ~~~~~~'
docker build ./viber-bot -t bot.jar
echo '~~~~~~ 3. BULDING CLIENT-FLASK IMAGE ~~~~~~'
docker build ./client -t client-flask
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
echo '~~~~~~ 2. STARTING CLIENT FLASK ~~~~~~'
docker-compose -f ./client/docker-compose.yml down
docker-compose -f ./client/docker-compose.yml up -d