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
docker build ./restapi -t restapi
echo '~~~~~~ 2. BULDING VIBER-BOT IMAGE ~~~~~~'
docker build ./bot -t bot
echo '~~~~~~ CREATING DOCKER NETWORK ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
docker network create --subnet=10.0.1.0/16 -d overlay nginx-net
echo '~~~~~~~~~~~ CLEARNING SERVICES'
docker stack rm $(docker stack ls )
sleep 15
echo '~~~~~~ STARTING DOCKER SERVICES ~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
echo '~~~~~~ 1. STARTING NGINX ~~~~~~'
docker stack deploy --compose-file ./nginx/docker-compose.yml nginx
echo '~~~~~~ 2. STARTING REST ~~~~~~'
docker stack deploy --compose-file ./restapi/docker-compose.yml rest
echo '~~~~~~ 3. STARTING VIBER-BOT ~~~~~~'
docker stack deploy --compose-file ./bot/docker-compose.yml bot
