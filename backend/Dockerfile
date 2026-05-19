FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY config-server/pom.xml config-server/
COPY identity-service/pom.xml identity-service/
COPY catalog-service/pom.xml catalog-service/
COPY borrowing-service/pom.xml borrowing-service/
COPY lib-gateway/pom.xml lib-gateway/
COPY lib-notifications/pom.xml lib-notifications/

RUN mvn dependency:go-offline -B

COPY eureka-server/src eureka-server/src
COPY config-server/src config-server/src
COPY identity-service/src identity-service/src
COPY catalog-service/src catalog-service/src
COPY borrowing-service/src borrowing-service/src
COPY lib-gateway/src lib-gateway/src
COPY lib-notifications/src lib-notifications/src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS eureka
WORKDIR /app
COPY --from=build /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS config
WORKDIR /app
COPY --from=build /app/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS identity
WORKDIR /app
COPY --from=build /app/identity-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS catalog
WORKDIR /app
COPY --from=build /app/catalog-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS borrowing
WORKDIR /app
COPY --from=build /app/borrowing-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS gateway
WORKDIR /app
COPY --from=build /app/lib-gateway/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS notifications
WORKDIR /app
COPY --from=build /app/lib-notifications/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]