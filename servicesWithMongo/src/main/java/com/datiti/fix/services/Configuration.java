package com.datiti.fix.services;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${rabbitmq.hostname:localhost}")
    private String amqpHostname;
    @Value("${rabbitmq.port:5672}")
    private int amqpPort;
    @Value("${rabbitmq.username}")
    private String amqpUsername;
    @Value("${rabbitmq.password}")
    private String amqpPassword;

    /**
     * Initialize RabbitMQ connection factory using parameters in application-default.properties
     *
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(amqpHostname, amqpPort);
        connectionFactory.setUsername(amqpUsername);
        connectionFactory.setPassword(amqpPassword);
        return connectionFactory;
    }

    @Bean
    public AmqpReceiver receiver() throws Exception {
        return new AmqpReceiver();
    }

}
