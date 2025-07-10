FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/secret-stash-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
