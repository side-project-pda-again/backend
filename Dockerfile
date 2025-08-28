# ---- build stage ----
FROM gradle:8.10.1-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# ---- runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]