package com.ok.chatbox;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;

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

    public static ChatCipher chatCipher;


    public static void main(String[] args) {
        //-ip 192.168.0.1 -port 5555 -uname indus -pbk FileName
        //--generate-pbk FileName


        ArgumentParser parser = ArgumentParsers.newFor("Kalyna Chat Client").build()
                .defaultHelp(true)
                .description("Client side Kalyna Encrypted Chat Application");
        Subparsers subparsers = parser.addSubparsers().dest("command");

        ArgumentParser createAccount = subparsers.addParser("login").description("Account Login")
                .defaultHelp(true).help("Account Login");
        ArgumentParser generateKeys = subparsers.addParser("key").description("Key Generation")
                .defaultHelp(true).help("Key Generation");
        createAccount.addArgument("-p","--port").metavar("PORT_NUMBER")
                .type(Integer.class).help("Port Number of Server")
                .setDefault(5555);
        createAccount.addArgument("-i","--ip").metavar("IP_ADDRESS")
                .type(String.class).help("IP Address of the Server").required(true);
        createAccount.addArgument("-u","--username").metavar("USER_NAME")
                .type(String.class).help("Unique Username of the Client").required(true);
        createAccount.addArgument("-k","--keys").metavar("FILE_PATH")
                .type(String.class)
                .help("File Path to the keys file to use to authenticate with the server")
                .setDefault("keys.key");

        generateKeys.addArgument("-g","--generate_keys").metavar("FILE_PATH")
                .type(String.class)
                .help("Generate Public and Private Key and store in the Specified File")
                .setDefault("keys.key");

        try {
            Namespace res = parser.parseArgs(args);
            if(res.get("command").equals("key")){
                ChatCipher.GeneratePublicKey(Path.of(res.get("generate_keys").toString()));
            }else{
                username = res.get("username");
                int port = res.get("port");
                String ip = res.get("ip").toString();
                Path keyFile = Path.of( res.get("keys").toString() );
                chatCipher = new ChatCipher(keyFile);
                ClientConn conn = new ClientConn(ip,port);

            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }



    }
}
