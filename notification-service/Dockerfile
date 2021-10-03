FROM ageofmobile/gradle-docker:latest

RUN mkdir /src
ADD . /src

WORKDIR /src

ENV GRADLE_USER_HOME=/src
RUN gradle build -x check -x pmdMain -x pmdTest -x checkstyleMain -x checkstyleTest -Dorg.gradle.daemon=false

RUN mv build/libs/notification-service-1.0.jar /opt/notification-service.jar && \
    rm -rf /src && \
    rm -rf /usr/local/gradle

ENV SERVER_PORT 8080
ENV USERGRID_ENDPOINT http://usergrid.f8.svc.cluster.local:8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/notification-service.jar"]