#!/bin/sh
echo '~~~~~~ BUILDING SPRING-BOOT SERVICES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~ 1. BULDING UTIL SERVICE ~~~~~~'
mvn -f ./utils clean install -DskipTests
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
echo '~~~~~~ CREATING DOCKER NETWORK ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
docker network create nginx-net
echo '~~~~~~ STARTING DOCKER SERVICES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~ 1. STARTING NGINX ~~~~~~'
docker-compose ./nginx down
docker-compose ./nginx up -d
echo '~~~~~~ 2. STARTING REST ~~~~~~'
docker-compose ./rest down
docker-compose ./rest up -d
echo '~~~~~~ 3. STARTING VIBER-BOT ~~~~~~'
docker-compose ./viber-bot down
docker-compose ./viber-bot up -d