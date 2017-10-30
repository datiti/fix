# Running samples

To run client/server samples, one can use pre-configured maven task with:

```bash
mvn exec:java
```

# Run rabbitmq in docker container

## Pre-requisite

* docker
* docker-compose

## Run rabbitmq with docker-compose [PREFERRED METHOD]

Compose file is: *docker-compose-rabbit.yml*

You need to create an environment file *.env* to set user/password of rabbitmq management plugin

Then run the following:

```bash
docker-compose.exe -f docker-compose-rabbit.yml up -d
``` 

*-d stands for daemon mode*


## Run rabbitmq

```bash
docker run -d --hostname fix-rabbit --name rabbit rabbitmq:3
```

## Connecting a docker app to the rabbitmq daemon

```bash
docker run --name some-app --link some-rabbit:rabbit -d application-that-uses-rabbitmq
```

## Run rabbitmq with management plugin enabled and amqp port enabled 

```bash
docker run -d --hostname fix-rabbit --name rabbit -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=password -p 15672:15672 -p 5672:5672 rabbitmq:3-management
```

With those arguments, default user and password for the management website are set.

