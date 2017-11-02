# Running samples

To run client/server samples, one can use pre-configured maven task with:

```bash
mvn exec:java
```

# Run services: choose between MongoDB or JPA (PostgreSQL) implementation

## services with MongoDB as underlying storage

```bash
mvn spring-boot:run
```

## services with JPA (PostgreSQL) as underlying storage

```bash
mvn spring-boot:run
```

To configure PostgreSQL database access, create a application-default.properties with the 
following properties:

```properties
spring.datasource.password=your_db_server_password
```

You can override any properties from application.properties.

# Docker - databases and message broker

## Pre-requisite

You need to have docker-compose installed:

```bash
pip install docker-compose
```

You need to specifiy ports we want to use in the host in a .env file:

```properties
RABBITUSER=admin
RABBITPASSWORD=a_password_for_rabbit
RABBITUIPORT=15672
RABBITAMQPPORT=5672
POSTGRES_PORT=5432
POSTGRES_PASSWORD=a_password_for_postgresql
MONGO_PORT=27017
```

## The services we need

* rabbitmq with admin UI enabled
* postgreSQL with an admin UI
* adminer - a DB management php script
* mongoDB

## Run docker-compose

To run docker-compose.yml file:

```bash
docker-compose up -d
```

**-d stands for daemon mode**

**specific docker-compose file can be specified with -f docker-compose-file.yml**

This docker-compose file will start all the services we need: rabbitmq with admin ui, postgresql with admin ui, mongodb.

## Verify what is going on

```bash
docker-compose logs
```

# Useful resources

## spring boot rest data

[REST server with spring data spring boot and postgresql](http://mrbool.com/rest-server-with-spring-data-spring-boot-and-postgresql/34023#ixzz4x8C2em9q)