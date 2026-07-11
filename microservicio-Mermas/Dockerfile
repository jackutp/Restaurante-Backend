FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY microservicio-Mermas/pom.xml microservicio-Mermas/pom.xml

RUN chmod +x mvnw
COPY . .
RUN ./mvnw package -DskipTests -pl microservicio-Mermas -am
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/microservicio-Mermas/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]