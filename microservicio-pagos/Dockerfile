FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY microservicio-pagos/pom.xml microservicio-pagos/pom.xml

RUN chmod +x mvnw
COPY . .
RUN ./mvnw package -DskipTests -pl microservicio-pagos -am
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/microservicio-pagos/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]