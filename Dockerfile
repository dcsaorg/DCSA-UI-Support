FROM eclipse-temurin:17-jre-alpine

EXPOSE 9091
ENV db_hostname dcsa_db
COPY run-in-container.sh /run.sh
RUN chmod +x /run.sh
COPY ui-support-service/src/main/resources/application.yml .
COPY ui-support-service/target/dcsa-ui-support-service.jar .
CMD ["/run.sh"]
