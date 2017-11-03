package com.datiti.fix.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import quickfix.*;
import quickfix.field.MsgType;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible of receiving FIX {@link Message} over RabbitMQ. It then decode the message using a FIX-4.2
 * {@link DataDictionary}.
 */
@RabbitListener(queues = "${amqp.fix.queue:com.datiti.fix.messages}")
public class AmqpReceiver {
    private static final Logger LOG = LogManager.getLogger(AmqpReceiver.class);
    private static final Logger PERF = LogManager.getLogger("PERF");
    private static DataDictionary DD = null;

    static {
        // we work with FIX-4.2 --> TODO: parametrize it in the future
        try {
            DD = new DataDictionary("FIX42.xml");
        } catch (ConfigError configError) {
            LOG.error("cannot load FIX42.xml FIX dictionary", configError);
        }
    }

    public AmqpReceiver() throws Exception {
        if (DD == null) {
            throw new Exception("No FIX dictionary available - FIX message decoding is not available");
        }
    }

    /**
     * Log the content of the given {@link Message}
     *
     * @param msg
     * @throws ConfigError
     * @throws FieldNotFound
     */
    private void processMessage(Message msg) throws ConfigError, FieldNotFound {
        long start = System.nanoTime();
        assert DD != null;
        // this value should go in Meta section of JSON object: see FIX to JSON technical spec
        String typeOfMessage = DD.getValueName(MsgType.FIELD, msg.getHeader().getString(MsgType.FIELD));
        LOG.info("{}", typeOfMessage);
        LOG.info("++++++++++++++++++++++++");
        processFieldMap(msg.getHeader());
        processFieldMap(msg);
        processFieldMap(msg.getTrailer());
        LOG.info("------------------------");
        long end = System.nanoTime();
        // TODO: should use metrics (http://metrics.dropwizard.io/) or a dedicated perfs logger
        PERF.info("AmqpReceiver:processMessage:{}:µs", Long.valueOf(TimeUnit.NANOSECONDS.toMicros(end-start)));
    }

    /**
     * Log the content of the given {@link FieldMap}
     *
     * @param fieldMap
     * @throws FieldNotFound
     */
    private void processFieldMap(FieldMap fieldMap) throws FieldNotFound {
        Iterator<Field<?>> fieldIterator = fieldMap.iterator();
        while (fieldIterator.hasNext()) {
            Field<?> field = fieldIterator.next();
            int tag = field.getTag();
            LOG.info("\"{}\": \"{}\"", DD.getFieldName(tag), fieldMap.getString(tag));
        }
    }

    /**
     * Receive incoming FIX {@link Message} as a byte array
     *
     * @param in the FIX {@link Message} as a byte array
     */
    @RabbitHandler
    public void receive(byte[] in) {
        String inmsg = new String(in);
        LOG.debug("receive message: {}", inmsg);
        Message msg = new Message();
        try {
            msg.fromString(inmsg, null, false);
            LOG.info("Receive FIX message: {}", msg);
            try {
                processMessage(msg);
            } catch (ConfigError configError) {
                configError.printStackTrace();
            } catch (FieldNotFound fieldNotFound) {
                fieldNotFound.printStackTrace();
            }
        } catch (InvalidMessage invalidMessage) {
            LOG.error("Cannot extract FIX message from: {}", in, invalidMessage);
        }
    }
}
