ARG MODULE
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
ARG MODULE
COPY ${MODULE} /app/${MODULE}
COPY pom.xml /app/pom.xml
RUN chmod +x /app/${MODULE}/mvnw && \
    /app/${MODULE}/mvnw -f /app/${MODULE}/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
ARG MODULE
WORKDIR /app
COPY --from=builder /app/${MODULE}/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]