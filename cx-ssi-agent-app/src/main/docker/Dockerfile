FROM openjdk:17-alpine

EXPOSE 8080

ARG APP_USER=docker
ARG APP_UID=10100
ARG JAR_FILE=target/*.jar

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

COPY --chown=$APP_USER:$APP_USER ${JAR_FILE} app.jar

USER $APP_USER:$APP_USER
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-Dspring.config.location=/app/", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar","app.jar"]
