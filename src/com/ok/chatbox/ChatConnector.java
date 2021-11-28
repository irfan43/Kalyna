package com.ok.chatbox;

import javax.crypto.KeyAgreement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ChatConnector {




    private static String Username;
    private static String Base64PublicKey;
    private static byte[] secret;
    private static ChatConsole cc;
    private static Thread ccThread;

    public static void ChatWith(String username,String base64PublicKey) throws IOException, GeneralSecurityException {
        Base64PublicKey = base64PublicKey;
        Username = username;
        cc = new ChatConsole();
        ccThread = new Thread(cc);

        DoExchange();
        ccThread.start();;

    }

    private static void DoExchange() throws GeneralSecurityException, IOException{
        System.out.println("starting Exchange ");
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
        kpg.initialize(512);
        KeyPair kp = kpg.generateKeyPair();
        PrivateKey pem = kp.getPrivate();
        PublicKey pub  = kp.getPublic();
        byte[] pubByte = pub.getEncoded();
        Signature sgn = ChatClient.chatCipher.getSigning();
        sgn.update(pubByte);
        byte[] signature = sgn.sign();

        System.out.println("Sending INIT");
        ChatClient.packetHandler.SendPacketINIT(Base64PublicKey,pubByte,signature);
        System.out.println("Sent INIT");
        ChatPacket cp = null;
        while (cp == null){
            cp = ChatClient.packetHandler.GetINITPacketFrom(Base64PublicKey);
            System.out.println("waiting");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("GOT INIT");
        String master = new String (cp.getData(), StandardCharsets.UTF_8);
        System.out.println(" got \n" + master);
        String[] d = master.split("\\r?\\n");
        byte[] theirPub  = Base64.getDecoder().decode(d[0]);
        byte[] theirSign  = Base64.getDecoder().decode(d[1]);
        Signature verifySgn = Signature.getInstance("SHA256withRSA");

        byte[] theirRSAPBKENC = Base64.getDecoder().decode(Base64PublicKey);
        PublicKey theirRSA = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(theirRSAPBKENC));

        verifySgn.initVerify(theirRSA);
        verifySgn.update(theirPub);
        if(!verifySgn.verify(theirSign)){
            System.out.println("BAD SIGN RECEIVED ");
            System.exit(1);
        }

        PublicKey theirDH = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(theirPub));

        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(pem);
        ka.doPhase(theirDH,true);
        byte[] s = ka.generateSecret();
        System.out.println( "got " + Base64.getEncoder().encodeToString( s ) );
        System.exit(0);
    }

    public static byte[] readBlock(ByteArrayInputStream is) throws IOException{
        int len = ByteBuffer.wrap(is.readNBytes(4)).getInt();
        if(len > is.available() || len < 0){
            throw new IllegalArgumentException("bad Packet");
        }
        return is.readNBytes(len);
    }
    public static void sendMsg(String toString) {
        if(secret != null) {
            cc.AddMessage(toString);
        }
    }
}
