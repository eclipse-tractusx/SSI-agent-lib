FROM openjdk:17-alpine as builder

COPY . .
RUN ./mvnw clean package
ARG JAR_FILE=cx-ssi-agent-app/target/*.jar
COPY ${JAR_FILE} app.jar

FROM openjdk:17-alpine

EXPOSE 8080

ARG APP_USER=docker
ARG APP_UID=10100

RUN addgroup --system "$APP_USER"

RUN adduser \
     --shell /sbin/nologin \
     --disabled-password \
     --gecos "" \
     --ingroup "$APP_USER" \
     --no-create-home \
     --uid "$APP_UID" \
     "$APP_USER"

WORKDIR /app

COPY --from=builder --chown=$APP_USER:$APP_USER app.jar app.jar

USER $APP_USER:$APP_USER
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-Dspring.config.location=/app/", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar","app.jar"]
