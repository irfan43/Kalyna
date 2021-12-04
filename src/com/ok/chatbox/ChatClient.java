package com.ok.chatbox;


import com.ok.kalyna.FileEncryption;
import com.ok.kalyna.Kalyna;
import com.ok.kalyna.KalynaIntegral;
import com.ok.kalyna.KalynaUtil;
import com.ok.server.ChatServer;
import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

public class ChatClient {

    public static String            username;
    public static ClientNetwork     rest;
    public static ClientNetwork     packetReader;
    public static PacketHandler     packetHandler;
    public static ChatCipher        chatCipher;


    public static void main(String[] args) {


        ArgumentParser parser = ArgumentParsers
                .newFor("Kalyna Application")
                .build()
                .defaultHelp(true)
                .description("Kalyna based File Encryption and E2EE (Client-Server Architecture) Chat Application");

        Subparsers subparsers = parser
                .addSubparsers()
                .dest("command");

        ArgumentParser fileEncryption = subparsers
                .addParser("file")
                .description("File Encryption/Decryption")
                .defaultHelp(true)
                .help("File Encryption/Decryption");

        ArgumentParser login = subparsers
                .addParser("login")
                .description("Account Login")
                .defaultHelp(true)
                .help("Account Login");

        ArgumentParser generateKeys = subparsers
                .addParser("key")
                .description("Key Generation")
                .defaultHelp(true)
                .help("Key Generation");

        ArgumentParser server = subparsers
                .addParser("server")
                .description("Server Mode")
                .defaultHelp(true)
                .help("Server Mode");

        ArgumentParser integral = subparsers
                .addParser("integral")
                .description("Integral Property")
                .defaultHelp(true)
                .help("Integral Property");

        //login parser
        login   .addArgument("-p","--port")
                .metavar("PORT_NUMBER")
                .type(Integer.class).help("Port Number of Server")
                .setDefault(5555);

        login   .addArgument("-i","--ip")
                .metavar("IP_ADDRESS")
                .type(String.class)
                .help("IP Address of the Server")
                .required(true);

        login   .addArgument("-u","--username")
                .metavar("USER_NAME")
                .type(String.class)
                .help("Unique Username of the Client")
                .required(true);

        login   .addArgument("-k","--keys")
                .metavar("FILE_PATH")
                .type(String.class)
                .help("File Path to the keys file to use to authenticate with the server")
                .setDefault("keys.key");

        //Key Gen Parser
        generateKeys.addArgument("-g","--generate_keys")
                .metavar("FILE_PATH")
                .type(String.class)
                .help("Generate Public and Private Key and store in the Specified File")
                .setDefault("keys.key");
        //File Encryption Parsers

        //This prevents the -e and -d to be used simultaneously
        MutuallyExclusiveGroup encDec = fileEncryption.addMutuallyExclusiveGroup()
                .required(true);

        encDec.addArgument("-e","--encrypt")
                .nargs(2)
                .type(String.class)
                .help("Encrypt the given File into the Output file")
                .metavar("INPUT_PATH", "OUTPUT_PATH");
        encDec.addArgument("-d","--decrypt")
                .nargs(2)
                .type(String.class)
                .help("Decrypt the given File into the Output file")
                .metavar("INPUT_PATH", "OUTPUT_PATH");

        fileEncryption.addArgument("-m","--mode")
                .metavar("<KEY_SIZE>_<BLOCK_SIZE>")
                .type(String.class)
                .help("Mode to set Kalyna Key and block sizes")
                .choices("128_128","256_128","256_256","512_256","512_512")
                .setDefault("256_256");

        //Server Parser
        server.addArgument("-p","--port")
                .metavar("PORT_NUMBER")
                .type(Integer.class)
                .help("Port the server will listen on")
                .setDefault(5555);

        //Integral Parser
        integral.addArgument("-b","--block_size")
                .type(Integer.class)
                .help("Block Size in Bits")
                .setDefault(128)
                .choices(128, 256 , 512);

        integral.addArgument("-a", "--all_index")
                .type(Integer.class)
                .help("ALL Property Index")
                .setDefault(0);

        integral.addArgument("-w","--whitening")
                .type(Boolean.class)
                .help("Include Pre Whitening Addition Modulus")
                .setDefault(false)
                .action(Arguments.storeTrue());

        try {
            Namespace res = parser.parseArgs(args);
            switch (res.getString("command")) {
                case "key" -> ChatCipher.GeneratePublicKey(Path.of(res.getString("generate_keys")));
                case "login" -> {
                    username = res.get("username");
                    int port = res.get("port");
                    String ip = res.getString("ip");
                    chatCipher = new ChatCipher(Path.of(res.getString("keys")));
                    packetHandler = new PacketHandler();
                    packetReader = new ClientNetwork(ip, port);
                    rest = new ClientNetwork(ip, port);
                    Thread pr = new Thread(packetReader);
                    pr.start();
                    UserList.showUserList();
                }
                case "file" -> {

                    //decoding the mode of the Cipher
                    String modeArg = res.getString("mode");
                    int keySize = Integer.parseInt(modeArg.substring(0, 3)) / 8;
                    int blockSize = Integer.parseInt(modeArg.substring(4, 7)) / 8;
                    int mode = Kalyna.getMode(blockSize, keySize);
                    List<String> files;
                    if (res.get("encrypt") != null) {
                        files = res.get("encrypt");
                    } else {
                        files = res.get("decrypt");
                        mode = -1;
                    }
                    FileEncryption.FileEncrypt(
                            null,
                            Path.of(files.get(0)),
                            Path.of(files.get(1)),
                            4096,
                            res.get("encrypt") != null,
                            mode
                    );
                }
                case "server" -> ChatServer.RunServer(res.get("port"));
                case "integral" -> {
                    byte[] constantV = new byte[res.getInt("block_size") / 8];
                    int allIndex = res.getInt("all_index");
                    boolean doWhitening = res.getBoolean("whitening");
                    if (0 > allIndex || allIndex >= res.getInt("block_size") / 8)
                        throw new IllegalArgumentException("Provided Illegal ALL Property Index " + allIndex);
                    int seed = (new Random()).nextInt();
                    Random r = new Random(seed);
                    r.nextBytes(constantV);
                    byte[][] constantValues = KalynaUtil.getState(constantV);
                    KalynaIntegral.kalynaIntegralProperty(
                            KalynaIntegral.generateDeltaSet(constantValues, allIndex),
                            doWhitening
                    );
                }
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

}
