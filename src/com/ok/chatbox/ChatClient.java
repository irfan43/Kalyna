package com.ok.chatbox;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.Set;

public class ChatClient {

    public static String username;
    public static PublicKey publicKey;
    private static PrivateKey privateKey;


    public static void main(String[] args) {
        //-ip 192.168.0.1 -port 5555 -uname indus -pbk FileName
        //--generate-pbk FileName


        // todo allow only generate pbk argument
        ArgumentParser parser = ArgumentParsers.newFor("Kalyna Chat Client").build()
                .defaultHelp(true)
                .description("Client side Kalyna Encrypted Chat Application");
        parser.addArgument("-p","--port").metavar("PORT_NUMBER")
                .type(Integer.class).help("Port Number of Server")
                .setDefault(5555);
        parser.addArgument("-i","--ip").metavar("IP_ADDRESS")
                .type(String.class).help("IP Address of the Server");
        parser.addArgument("-u","--username").metavar("USER_NAME")
                .type(String.class).help("Unique Username of the Client");
        parser.addArgument("-g","--generate_keys").metavar("FILE_PATH")
                .type(String.class)
                .help("Generate Public and Private Key and store in the Specified File");
        parser.addArgument("-pbk","--public-key").metavar("FILE_PATH")
                .type(String.class)
                .help("FilePath to the public key file to use to authenticate with the server");

        try {
            Namespace res = parser.parseArgs(args);
            username = res.get("username");

            ClientConn conn = new ClientConn(res.get("ip"),res.get("port"));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }



    }
}
