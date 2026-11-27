FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY BackEnd/pom.xml BackEnd/pom.xml
COPY BackEnd/src BackEnd/src
RUN mkdir -p BackEnd/target/dependency \
    && mvn -f BackEnd/pom.xml -DskipTests package dependency:copy-dependencies \
    -DincludeScope=runtime -DoutputDirectory=target/dependency

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/BackEnd/target/backend-1.0.0.jar /app/backend.jar
COPY --from=build /workspace/BackEnd/target/dependency /app/lib

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "/app/backend.jar:/app/lib/*", "conexaosolidaria.Main"]