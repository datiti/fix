package com.datiti.fix.app;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * FIX sample server for testing purpose
 */
public class SampleServerApp implements Application {
    private static final Logger LOG = LogManager.getLogger(SampleServerApp.class);
    private static final String CONFIG_FILE_NAME = "/quickfixj-server.ini";


    static {
        ToStringBuilder.setDefaultStyle(ToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * Exit status of the application: those are used as log_and_exit method parameter
     */
    public enum EXIT_STATUS {
        ERROR_CONFIG(-1),
        ERROR_LOAD_CONFIG(-2),
        ERROR_START_ACCEPTOR(-3),;

        private final int val;

        private EXIT_STATUS(int val) {
            this.val = val;
        }

        public int val() {
            return val;
        }
    }

    @Override
    public void onCreate(SessionID sessionID) {
        LOG.info("onCreate: {}", sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        LOG.info("onLogon: {}", sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        LOG.info("onLogout: {}", sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        LOG.info("toAdmin: {} {}", sessionID, message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        LOG.info("fromAdmin: {} {}", sessionID, message);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        LOG.info("toApp: {} {}", sessionID, message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        LOG.info("fromApp: {} {}", sessionID, message);
    }

    /**
     * Log the given message and Exit with given status
     *
     * @param status
     * @param message
     * @param values
     */
    public static void log_and_exit(EXIT_STATUS status, String message, Object... values) {
        LOG.fatal(message, values);
        System.exit(status.val());
    }

    /**
     * Initialize quickfix stack, giving back the Acceptor (SocketAcceptor in our case)
     *
     * @param app
     */
    public static Acceptor init_and_start_acceptor(Application app) {
        SessionSettings settings = null;
        InputStream configIS = SampleServerApp.class.getResourceAsStream(CONFIG_FILE_NAME);
        if (configIS == null) {
            log_and_exit(EXIT_STATUS.ERROR_CONFIG, "Config file {} not found in current classpath", CONFIG_FILE_NAME);
        }
        try {
            settings = new SessionSettings(configIS);
        } catch (ConfigError configError) {
            log_and_exit(EXIT_STATUS.ERROR_LOAD_CONFIG, "Problem loading config file {}", CONFIG_FILE_NAME, configError);
        }
        // Implementation note: a JdbcStoreFactory can also be used
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        // Implementation note: a FileLogFactory, JdbcLogFactory or owned implem can be used
        LogFactory logFactory = new SLF4JLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();
        Acceptor acceptor = null;
        try {
            acceptor = new SocketAcceptor(app, storeFactory, settings, logFactory, messageFactory);
            acceptor.start();
        } catch (ConfigError configError) {
            log_and_exit(EXIT_STATUS.ERROR_START_ACCEPTOR, "Cannot create SocketAcceptor", configError);
        }
        return acceptor;
    }

    public static void main(String[] args) {
        LOG.info("Starting sample FIX server");

        SampleServerApp sampleServerApp = new SampleServerApp();
        Acceptor acceptor = init_and_start_acceptor(sampleServerApp);

        LOG.info("Acceptor successfully initialized, active sessions: {}", acceptor.getSessions());

        // do what you want with the acceptor
        boolean running = true;
        while(running) {
            System.out.println("press <enter> to quit");
            try {
                System.in.read();
            } catch (IOException e) {
                LOG.warn("problem reading input from standard input", e);
            }
            running = false;
        }

        // stopping the acceptor properly
        acceptor.stop();

        LOG.info("Quiting sample FIX server");
    }
}
