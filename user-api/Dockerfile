FROM ageofmobile/gradle-docker:latest

RUN mkdir /src
ADD . /src

WORKDIR /src

ENV GRADLE_USER_HOME=/src
RUN gradle build -x check -x pmdMain -x pmdTest -x checkstyleMain -x checkstyleTest -Dorg.gradle.daemon=false

RUN mv build/libs/user-api-1.0.jar /opt/user-api.jar && \
    rm -rf /src && \
    rm -rf /usr/local/gradle

ENV SERVER_PORT 8080

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/user-api.jar"]