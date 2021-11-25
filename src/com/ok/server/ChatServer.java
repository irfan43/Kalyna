package com.ok.server;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class ChatServer {

    public static ClientList clientList;
    public static void main(String[] args) {
        // -p port
        int port = 5555;
        ArgumentParser parser = ArgumentParsers.newFor("Kalyna Chat Server").build()
                .defaultHelp(true)
                .description("Server side Kalyna Encrypted Chat Application");
        parser.addArgument("-p","--port").metavar("PORT_NUMBER")
                        .type(Integer.class).help("Port Number for Server to Port Bind to")
                        .setDefault(5555);
        try {

            Namespace res = parser.parseArgs(args);
            System.out.println("Server Running");
            clientList = new ClientList();
            serverListener.StartListening(res.get("port"));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }

    }
}
