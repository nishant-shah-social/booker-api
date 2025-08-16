# Stage 1: Build the application
FROM maven:3.9.3-eclipse-temurin-20 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml, testng.xml and source code
COPY pom.xml /app
COPY src /app/src
COPY testng.xml /app/testng.xml

# Install unzip, curl, and Allure CLI
RUN apt-get update && apt-get install -y unzip curl ca-certificates \
    && update-ca-certificates \
    && curl -L -o allure.zip https://github.com/allure-framework/allure2/releases/download/2.27.0/allure-2.27.0.zip \
    && unzip allure.zip -d /opt \
    && rm allure.zip \
    && ln -s /opt/allure-2.27.0/bin/allure /usr/bin/allure

# Expose port
EXPOSE 8080

# Run tests on container start, then serve Allure report
CMD mvn clean test -DsuiteXmlFile=testng.xml -Dmaven.test.failure.ignore=true && \
    allure serve target/allure-results --host 0.0.0.0 --port 8080