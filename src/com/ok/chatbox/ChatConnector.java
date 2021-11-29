package com.ok.chatbox;

import com.ok.kalyna.Kalyna;
import com.ok.kalyna.KalynaCFB;
import com.ok.kalyna.KalynaHash;

import javax.crypto.KeyAgreement;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ChatConnector {



    private static int mode = Kalyna.KALYNA_512KEY_512BLOCK;
    private static String TheirUsername;
    private static String PrefaceTheirUsername;
    private static String PrefaceOurUsername;
    private static String Base64PublicKey;
    private static byte[] secret;
    public static ChatConsole cc;
    private static Thread ccThread;



    public static void ChatWith(String username,String base64PublicKey) throws IOException, GeneralSecurityException {
        Base64PublicKey = base64PublicKey;
        TheirUsername = username;
        cc = new ChatConsole();
        ccThread = new Thread(cc);

        BuildPrefaces();

        DoExchange();
        ccThread.start();;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (ccThread.isAlive()){
            ChatPacket cp = ChatClient.packetHandler.GetPacketFrom(Base64PublicKey);
            if( cp != null) {
                receivedMsg(cp);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void BuildPrefaces() {
        int len = Math.max(TheirUsername.length(),ChatClient.username.length()) + 2 ;
        PrefaceTheirUsername = PadString(TheirUsername,len);
        PrefaceOurUsername = PadString(ChatClient.username,len);
    }

    private static String PadString(String input, int target){
        int pad = target - input.length();
        int padLeft = pad/2;
        int padRight = pad - padLeft;

        return "[" + " ".repeat(Math.max(0, padLeft)) +
                input +
                " ".repeat(Math.max(0, padRight)) +
                "]: ";
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
        System.out.println("waiting for there init");
        while (cp == null){
            cp = ChatClient.packetHandler.GetINITPacketFrom(Base64PublicKey);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("GOT INIT");
        String master = new String (cp.getData(), StandardCharsets.UTF_8);
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
        secret = KalynaHash.Hash(s,Kalyna.getKeySize(mode) );
    }

    public static void receivedMsg(ChatPacket cp){
        String[] pack = new String(cp.getData(),StandardCharsets.UTF_8).split("\\r?\\n");
        byte[] data    = Base64.getDecoder().decode(pack[0]);
        byte[] iv      = Base64.getDecoder().decode(pack[1]);
        byte[] salt    = Base64.getDecoder().decode(pack[2]);
        KalynaCFB k = new KalynaCFB( secret, mode, iv, salt);
        data = k.Update(data);

        String str = new String(data);

        cc.AddMessage( PrefaceTheirUsername + str);

    }
    public static void sendMsg(String msg) throws IOException {
        cc.AddMessage(  PrefaceOurUsername + msg);
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        KalynaCFB k = new KalynaCFB(secret,mode);
        data = k.Update(data);
        byte[] IV = k.getIV();
        byte[] SALT = k.getSALT();

        String EncryptedPack = Base64.getEncoder().encodeToString(data) + "\n"
                + Base64.getEncoder().encodeToString(IV) + "\n"
                + Base64.getEncoder().encodeToString(SALT) + "\n";

        ChatClient.packetHandler.SendMessagePacket(Base64PublicKey,EncryptedPack.getBytes(StandardCharsets.UTF_8));

    }
}
