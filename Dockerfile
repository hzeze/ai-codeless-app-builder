FROM eclipse-temurin:21-alpine AS builder

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./

RUN ./mvnw dependency:go-offline -B -s .mvn/settings.xml

COPY src ./src

RUN ./mvnw clean package -DskipTests -s .mvn/settings.xml

FROM eclipse-temurin:21-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8123

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
