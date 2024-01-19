#Scripts needs next docker secrets in order to start services correctly
#echo "test" | docker secret create db_pw -
#echo "root" | docker secret create db_user -

#!/bin/sh
echo '~~~~~~ BUILDING SPRING-BOOT SERVICES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
mvn clean install -DskipTests
echo '~~~~~~ BUILDING DOCKER IMAGES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~ 1. BULDING REST IMAGE ~~~~~~'
docker build ./restapi -t texas-restapi
echo '~~~~~~ 2. BULDING VIBER-BOT IMAGE ~~~~~~'
docker build ./bot -t texas-bot
echo '~~~~~~ CREATING DOCKER NETWORK ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
docker network create --subnet=10.0.1.0/16 -d overlay nginx-net
sleep 15
echo '~~~~~~ STARTING DOCKER SERVICES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~ 1. STARTING NGINX ~~~~~~'
docker stack deploy --compose-file ./nginx/docker-compose.yml nginx
sleep 5
echo '~~~~~~ 2. STARTING REST ~~~~~~'
docker stack deploy --compose-file ./restapi/docker-compose.yml texas-rest
sleep 5
echo '~~~~~~ 3. STARTING VIBER-BOT ~~~~~~'
docker stack deploy --compose-file ./bot/docker-compose.yml texas-bot
