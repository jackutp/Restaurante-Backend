ARG MODULE=Gateway-Server
FROM eclipse-temurin:25-jdk as builder
WORKDIR /app
ARG MODULE=Gateway-Server
COPY ${MODULE} /app/${MODULE}
COPY pom.xml .
#RUN ./mvnw clean package -DskipTests
RUN chmod +x /app/${MODULE}/mvnw && /app/${MODULE}/mvnw -f /app/${MODULE}/pom.xml clean package -DskipTests

FROM eclipse-temurin:25-jre
ARG MODULE=Gateway-Server
WORKDIR /app
COPY --from=builder /app/${MODULE}/target/*.jar app.jar
#COPY ${MODULE}/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
