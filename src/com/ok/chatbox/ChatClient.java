package com.ok.chatbox;


import com.ok.kalyna.FileEncryption;
import com.ok.kalyna.Kalyna;
import com.ok.server.ChatServer;
import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public class ChatClient {

    public static String username;
    public static ClientNetwork rest;
    public static ClientNetwork packetReader;
    public static PacketHandler packetHandler;
    public static PublicKey publicKey;
    private static PrivateKey privateKey;


    public static ChatCipher chatCipher;


    public static void main(String[] args) {

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
        login.addArgument("-i","--ip").metavar("IP_ADDRESS")
                .type(String.class).help("IP Address of the Server").required(true);
        login.addArgument("-u","--username").metavar("USER_NAME")
                .type(String.class).help("Unique Username of the Client").required(true);
        login.addArgument("-k","--keys").metavar("FILE_PATH")
                .type(String.class)
                .help("File Path to the keys file to use to authenticate with the server")
                .setDefault("keys.key");

        generateKeys.addArgument("-g","--generate_keys").metavar("FILE_PATH")
                .type(String.class)
                .help("Generate Public and Private Key and store in the Specified File")
                .setDefault("keys.key");

        try {
            Namespace res = parser.parseArgs(args);
            switch (res.get("command").toString()) {
                case "key":
                    ChatCipher.GeneratePublicKey(Path.of(res.get("generate_keys").toString()));
                    break;
                case "login":
                    username = res.get("username");
                    int port = res.get("port");
                    String ip = res.get("ip").toString();
                    Path keyFile = Path.of(res.get("keys").toString());
                    chatCipher = new ChatCipher(keyFile);
                    packetHandler = new PacketHandler();
                    packetReader = new ClientNetwork(ip, port);
                    Thread pr = new Thread(packetReader);
                    pr.start();
                    rest = new ClientNetwork(ip, port);

                    UserList.showUserList();
                    break;
                case "file":
                    String modeArg = res.get("mode").toString();
                    int keySize = Integer.parseInt(modeArg.substring(0,3));
                    int blockSize = Integer.parseInt(modeArg.substring(4,7));

                    int mode = Kalyna.getMode(blockSize,keySize);

                    byte[] key = FileEncryption.getKey( getPassword(), Kalyna.getKeySize(mode));
                    boolean encrypt = res.getAttrs().containsKey("encrypt");
                    Path in;
                    Path out;
                    String[] t;
                    if(encrypt){
                        t = res.get("encrypt");
                    }else {
                        t = res.get("decrypt");
                    }
                    in = Path.of(t[0]);
                    out = Path.of(t[1]);


                    FileEncryption.FileEncrypt(
                            key,
                            in,
                            out,
                            4096,
                            encrypt,
                            mode
                            );
                    break;
                case "server":
                    ChatServer.RunServer(res.get("port"));
                    break;
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }



    }

    private static char[] getPassword() {
        Console con = System.console();
        if(con == null){
            Scanner s = new Scanner(System.in);
            String rtr = s.nextLine();
            return rtr.toCharArray();
        }
        return con.readPassword();
    }
}
