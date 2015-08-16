FROM amannm/java8-docker-base
MAINTAINER Amann Malik "amann.malik@yodle.com"
ADD build/libs/barebones-service.jar /srv/barebones-service.jar
EXPOSE 8080
ENTRYPOINT java -Xmx128m -jar /srv/facebook-audience-service.jar