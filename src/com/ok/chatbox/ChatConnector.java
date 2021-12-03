package com.ok.chatbox;

import com.ok.kalyna.Kalyna;
import com.ok.kalyna.KalynaCFB;
import com.ok.kalyna.KalynaHash;

import javax.crypto.KeyAgreement;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class ChatConnector{



    private final int           mode = Kalyna.KALYNA_512KEY_512BLOCK;
    private final String        TheirUsername;
    private       String        PrefaceTheirUsername;
    private       String        PrefaceOurUsername;
    private final String        Base64PublicKey;
    private       byte[]        secret;
    public        ChatConsole   console;
    private final Thread        consoleThread;

    public final  Object        lock = new Object();


    /**
     * Chat Connector Connects the <code>ChatConsole</code> and <code>PacketHandler</code>
     * It initializes ChatConsole and performs a key exchange
     * After which it sends encrypted text packets received from ChatConsole
     * And
     * displays received text packets from PacketHandler on ChatConsole
     *
     * @param username The username of the person you are wishing to chat with
     * @param base64PublicKey The public Key of the person you are wishing to chat with encoded in Base64
     */
    public ChatConnector(String username,String base64PublicKey) {
        Base64PublicKey     = base64PublicKey;
        TheirUsername       = username;
        //initialize the console and console thread
        console             = new ChatConsole(this);
        consoleThread       = new Thread(console);
        BuildPrefaces();

    }

    /**
     * initialize Both Prefaces
     * They are used when adding a message to the Chat Console
     */
    private void BuildPrefaces() {
        int len = Math.max(TheirUsername.length(),ChatClient.username.length()) + 2 ;

        PrefaceTheirUsername    = PadString(TheirUsername,len);
        PrefaceOurUsername      = PadString(ChatClient.username,len);
    }

    private String PadString(String input, int target){
        int pad = target - input.length();
        int padLeft = pad/2;
        int padRight = pad - padLeft;

        return "[" +
                " ".repeat(Math.max(0, padLeft)) +
                input +
                " ".repeat(Math.max(0, padRight)) +
                "]: ";
    }

    private KeyPair GenerateDHKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
        kpg.initialize(512);
        return kpg.generateKeyPair();
    }
    private void SendINITPacket(PublicKey DHPublicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
        byte[] pubByte  = DHPublicKey.getEncoded();
        Signature sgn   = ChatClient.chatCipher.getSigning();
        sgn.update(pubByte);
        byte[] signature = sgn.sign();

        ChatClient.packetHandler.SendPacketINIT(Base64PublicKey,pubByte,signature);
    }

    private ChatPacket GetTheirINIT(){
        ChatPacket cp;
        do{
            cp = ChatClient.packetHandler.GetINITPacketFrom(Base64PublicKey);
            if(cp == null) {
                try {
                    synchronized (lock) {
                        lock.wait();
                    }
                } catch (InterruptedException ignored) {}
            }
        }while (cp == null);
        return cp;
    }
    private PublicKey getTheirPublicKey(ChatPacket init) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, BadINITSignException {
        String theirINITBase64 = new String (init.getData(), StandardCharsets.UTF_8);

        String[] d = theirINITBase64.split("\\r?\\n");
        byte[] theirPub  = Base64.getDecoder().decode(d[0]);
        byte[] theirSign  = Base64.getDecoder().decode(d[1]);

        Signature verifySgn = Signature.getInstance("SHA256withRSA");

        byte[] theirRSAPBKENC = Base64.getDecoder().decode(Base64PublicKey);
        PublicKey theirRSA = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(theirRSAPBKENC));

        verifySgn.initVerify(theirRSA);
        verifySgn.update(theirPub);
        if(!verifySgn.verify(theirSign))
            throw new BadINITSignException("Invalid Sign");

        return KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(theirPub));
    }
    private byte[] GenerateSecret(PrivateKey ourPrivateKey,PublicKey theirPublicKey) throws InvalidKeyException, NoSuchAlgorithmException {
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(ourPrivateKey);
        ka.doPhase(theirPublicKey,true);
        byte[] s = ka.generateSecret();
        return KalynaHash.Hash( s,Kalyna.getKeySize(mode) );
    }
    private void DoExchange() throws GeneralSecurityException, IOException{
        System.out.println("starting Exchange ");
        KeyPair DHKeyPair = GenerateDHKeyPair();
        SendINITPacket(DHKeyPair.getPublic());
        System.out.println("Sent Our Public Key\nWaiting For Their Public Key");
        PublicKey theirPublicKey = getTheirPublicKey(GetTheirINIT());
        System.out.println("GOT Their Public Key");
        secret = GenerateSecret(DHKeyPair.getPrivate(),theirPublicKey);
    }

    public void receivedMsg(ChatPacket cp){
        String[] pack = new String(cp.getData(),StandardCharsets.UTF_8).split("\\r?\\n");
        byte[] data    = Base64.getDecoder().decode(pack[0]);
        byte[] iv      = Base64.getDecoder().decode(pack[1]);
        byte[] salt    = Base64.getDecoder().decode(pack[2]);
        byte[] mac     = Base64.getDecoder().decode(pack[3]);
        KalynaCFB k = new KalynaCFB( secret, mode, iv, salt);
        data = k.Update(data);

        //if the calculated mac is equal to given mac
        //      we add the message to the console,
        //      else we ignore it
        if(Arrays.equals(mac,k.getMAC())) {
            String str = new String(data);
            console.AddMessage(PrefaceTheirUsername + str);
        }
    }
    public void sendMsg(String msg) throws IOException {
        console.AddMessage(  PrefaceOurUsername + msg);
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        KalynaCFB k = new KalynaCFB(secret,mode);
        data = k.Update(data);
        byte[] IV   = k.getIV();
        byte[] SALT = k.getSALT();
        byte[] MAC  = k.getMAC();

        String EncryptedPack = Base64.getEncoder().encodeToString(data) + "\n"
                + Base64.getEncoder().encodeToString(IV) + "\n"
                + Base64.getEncoder().encodeToString(SALT) + "\n"
                + Base64.getEncoder().encodeToString(MAC) + "\n";

        ChatClient.packetHandler.SendMessagePacket(Base64PublicKey,EncryptedPack.getBytes(StandardCharsets.UTF_8));

    }


    public void Open() {
        try {
            DoExchange();
        }catch (BadINITSignException e){
            System.out.println("Got BAD Sign");
            return;
        }catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        consoleThread.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ChatPacket cp;
        while (consoleThread.isAlive()){

            do{
                cp = ChatClient.packetHandler.GetPacketFrom(Base64PublicKey);
                if( cp != null) receivedMsg(cp);
            }while (cp != null);

            try {
                synchronized (lock){lock.wait();}
            } catch (InterruptedException ignored){}
        }
    }
}
