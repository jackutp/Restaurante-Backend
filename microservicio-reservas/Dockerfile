FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY microservicio-reservas/pom.xml microservicio-reservas/pom.xml

RUN chmod +x mvnw
COPY . .
RUN ./mvnw package -DskipTests -pl microservicio-reservas -am
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/microservicio-reservas/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]