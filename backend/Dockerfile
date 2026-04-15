FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY lib-gateway/pom.xml lib-gateway/
COPY lib-core/pom.xml lib-core/
COPY lib-notifications/pom.xml lib-notifications/
COPY borrowing-service/pom.xml borrowing-service/

RUN mvn dependency:go-offline -B

COPY lib-gateway/src lib-gateway/src
COPY lib-core/src lib-core/src
COPY lib-notifications/src lib-notifications/src
COPY borrowing-service/src borrowing-service/src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS gateway
WORKDIR /app
COPY --from=build /app/lib-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS core
WORKDIR /app
COPY --from=build /app/lib-core/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS notifications
WORKDIR /app
COPY --from=build /app/lib-notifications/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS borrowing
WORKDIR /app
COPY --from=build /app/borrowing-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
