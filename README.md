# Quizzes RESTful API

Quizzes RESTful API is back-end application that provides the support and the data required by Quizzes Web Application.
When the data is coming from an external repository (a LMS), this back-end will be in charge to gather the information
and prepare it in a way that Quizzes Web Application can use it appropriately.

## Overview of the whole architecture

The next diagram is an overview the whole architecture, where the most important pieces of software are present and numerated.

#<< image goes here >>
#<< description of image goes here >>

## Technology Stack

* [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads)
* [Spring](https://spring.io/docs)
* [Spring Boot](http://projects.spring.io/spring-boot)
* [Spring WebSocket](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/websocket.html)
* [PostgreSQL](https://www.postgresql.org)
* [ActiveMQ](http://activemq.apache.org)
* [HAProxy](http://www.haproxy.org)
* [Varnish](https://varnish-cache.org)
* [Flyway](https://flywaydb.org)
* [Swagger](http://swagger.io)
* [Gradle](https://gradle.org)

## Project Structure

This project follows the project structure suggested by Gradle and required by Spring Boot.

## Prerequisites

Some prerequisites to start up the RESTful API server are:

* Install and start up ActiveMQ server. To start up the server you can use `[activemq_install_dir]/bin/activemq console`
* Install and start up PostgreSQL server
* Create Quizzes database using  `psql -U postgres -c 'CREATE DATABASE quizzes;'`

## Start up RESTful API server

* To build and start up the server you can use `gradle clean build && java -jar build/libs/quizzes-api-<version>.jar`
* To start up the server in debug mode you can use `gradle bootRun`

## Project Tests

* To run the project tests you can use `gradle test`

## RESTful API Documentation with Swagger

* To see the RESTful API documentation you can access this url `http://localhost:8080/swagger-ui.html`