FROM eclipse-temurin:17-jdk-alpine as build-deps
COPY settings.xml /root/.m2/settings.xml
RUN mkdir /tmp/build-dir
WORKDIR /tmp/build-dir

COPY .mvn .mvn
COPY mvnw mvnw
COPY ui-support-persistence/pom.xml ui-support-persistence/pom.xml
COPY ui-support-transfer-obj/pom.xml ui-support-transfer-obj/pom.xml
COPY ui-support-service/pom.xml ui-support-service/pom.xml
COPY ui-support-integration-tests/pom.xml ui-support-integration-tests/pom.xml
COPY pom.xml pom.xml
RUN ./mvnw -B dependency:go-offline
COPY ui-support-persistence ui-support-persistence
COPY ui-support-transfer-obj ui-support-transfer-obj
COPY ui-support-service ui-support-service
COPY ui-support-integration-tests ui-support-integration-tests
RUN ./mvnw -gs /root/.m2/settings.xml -B package


FROM eclipse-temurin:17-jre-alpine

EXPOSE 9091
ENV db_hostname dcsa_db
COPY run-in-container.sh /run.sh
RUN chmod +x /run.sh
COPY --from=build-deps /tmp/build-dir/ui-support-service/src/main/resources/application.yml .
COPY --from=build-deps /tmp/build-dir/ui-support-service/target/dcsa-ui-support-service.jar .
CMD ["/run.sh"]
