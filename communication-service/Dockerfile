FROM ageofmobile/gradle-docker:latest

RUN mkdir /src
ADD . /src

WORKDIR /src

ENV GRADLE_USER_HOME=/src
RUN gradle build -x check -x pmdMain -x pmdTest -x checkstyleMain -x checkstyleTest -Dorg.gradle.daemon=false

RUN mv build/libs/communication-service-1.0.jar /opt/communication-service.jar && \
    rm -rf /src && \
    rm -rf /usr/local/gradle

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar /opt/communication-service.jar