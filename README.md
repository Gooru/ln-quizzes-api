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
* Install ActiveMQ server.
* `[activemq_install_dir]/bin/activemq console` to start up ActiveMQ server.
* Install and start up PostgreSQL server.
* `psql -U postgres -c 'CREATE DATABASE quizzes;'` to create Quizzes database.
* [Install and configure Varnish.](docs/varnish-configuration.md)

## How to use Flyway?
The most important functionalities with Flyway are:
* `gradle flywayClean` cleans up and removes any previous migration in the DB schema (Note: this is only for development and test environments)
* `gradle flywayMigrate` runs the migrations to build the DB schema.

## How to build the application?
To build the application you can do the following steps:
* `gradle clean` to delete any previous distribution.
* `gradle build` to build a new application distribution.

## How to generate JOOQ models?
To use JOOQ code generator you can do this:
* `gradle clean build -PgenerateJooq` to clean, build and generate JOOQ models.
* `gradle generateQuizzesJooqSchemaSource -PgenerateJooq` to generate JOOQ models.

## Start up RESTful API server
To start up the server there are several options:
* `java -jar build/libs/quizzes-api-<version>.jar` to start up the server.
* `gradle bootRun` to start up the server in debug mode.

## Project Tests
* To execute the project tests you can use `gradle test`

## RESTful API Documentation with Swagger
* To see the RESTful API documentation you can access this url `http://localhost:8080/swagger-ui.html`

## Docker and Docker-Compose
Docker and Docker-Compose can be use to run the application without installing its dependencies (PostgreSQL and ActiveMQ).

### Prerequisites

- Docker ([Linux](https://docs.docker.com/engine/installation/linux/ubuntulinux/), [Mac](https://docs.docker.com/docker-for-mac/))
- [Docker-Compose](https://docs.docker.com/compose/install/)

`docker-compose up`
