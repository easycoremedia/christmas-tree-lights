package com.easycore.ChristmasTreeLights;

import com.easycore.ChristmasTreeLights.firebase.ChangeWatcher;
import com.easycore.ChristmasTreeLights.led.LedController;
import org.apache.commons.cli.*;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;

public class App {

    private CmdArguments cmd;

    private static Options options;

    static {
        options = new Options();
        options.addOption("c", "config", true, "required, Path to config file");
        options.addOption("e", "endpoints", true, "required, WebSocket endpoint to connect");
    }

    public void parseArguments(String[] args) throws HelpException {
        if (args.length > 0 && "help".equals(args[0])) {
            throw new HelpException();
        }

        try {
            cmd = CmdArguments.parse(options, args);
        } catch (ParseException e) {
            throw new HelpException("Invalid parameter syntax.");
        }
    }

    public ChangeWatcher prepareWatcher() throws IOException, DeploymentException {
        ChangeWatcher watcher = new ChangeWatcher();
        watcher.setConfiguration(cmd.getConfigFile());
        LedController controller = new LedController(cmd.getWebSocketEndpoint());
        watcher.setChangeHandler(controller);
        return watcher;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ant", options);
    }

    /**
     * Exception signaling help should be displayed.
     */
    public class HelpException extends Exception {
        private boolean empty;

        public HelpException() {
            super();

            empty = true;
        }

        public HelpException(String message) {
            super(message);

            empty = false;
        }

        public void exit() {
            int exitCode = 0;
            if (!empty) {
                System.err.printf("%s\n\n", getMessage());
                exitCode = 1;
            }

            App.this.printHelp();
            System.exit(exitCode);
        }
    }

    /**
     * Definition of Cmd arguments.
     */
    private static class CmdArguments {
        private String configFile;

        private String webSocketEndpoint;

        private CmdArguments(String configFile, String webSocketEndpoint) {
            this.configFile = configFile;
            this.webSocketEndpoint = webSocketEndpoint;
        }

        String getConfigFile() {
            return configFile;
        }

        URI getWebSocketEndpoint() {
            String uriEndpoint = webSocketEndpoint;
            if (!uriEndpoint.startsWith("ws://")) {
                uriEndpoint = "ws://" + uriEndpoint;
            }

            return URI.create(uriEndpoint);
        }

        static CmdArguments parse(Options options, String[] args) throws ParseException {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            String configFilePath = cmd.getOptionValue("c");
            String endpoint = cmd.getOptionValue("e");
            if (configFilePath == null || endpoint == null) {
                throw new ParseException("Invalid syntax");
            }

            return new CmdArguments(configFilePath, endpoint);
        }
    }
}
