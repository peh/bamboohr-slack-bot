FROM java:8-alpine

RUN apk add --no-cache wget

WORKDIR /app

EXPOSE 9000

#RUN wget from github release

COPY current.jar /app/current.jar

CMD ["java", "-jar", "/app/current.jar", "-Dredis.host=redis"]
