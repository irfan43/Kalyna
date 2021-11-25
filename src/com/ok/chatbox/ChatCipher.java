package com.ok.chatbox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ChatCipher {

    private static final String StartOfPublicKey   = "---   Begin Public Key  ---";
    private static final String EndOfPublicKey     = "---    End Public Key   ---";
    private static final String StartOfPrivateKey  = "---  Begin Private Key  ---";
    private static final String EndOfPrivateKey    = "---   End Private Key   ---";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public ChatCipher(Path PBKFile) throws IOException {
        loadKeyPair(PBKFile);
        System.out.println(" pbk " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        System.out.println(" prv " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
    }
    private void loadKeyPair(Path PBKFile) throws IOException {
        InputStream inputStream = Files.newInputStream(PBKFile);

        byte[] publicKeyEnc;
        byte[] privateKeyEnc;

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)) ){
            privateKeyEnc = ReadKey(bufferedReader,StartOfPrivateKey,EndOfPrivateKey);
            publicKeyEnc  = ReadKey(bufferedReader,StartOfPublicKey,EndOfPublicKey);
        }
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyEnc);
        PKCS8EncodedKeySpec prvKeySpec = new PKCS8EncodedKeySpec(privateKeyEnc);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(prvKeySpec);
            publicKey = keyFactory.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();

        }

    }
    public static byte[] ReadKey(BufferedReader reader,String start,String end) throws IOException {

        StringBuilder sb = new StringBuilder();
        while(!reader.readLine().equals(start));
        do{
            String line = reader.readLine();
            if(line.equals(end))
               break;
            sb.append(line);
        }while (true);

        return Base64.getDecoder().decode(sb.toString());
    }
    public static void GeneratePublicKey(Path PBKFile) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512);
        KeyPair kp = kpg.generateKeyPair();

        PrivateKey pvk = kp.getPrivate();
        PublicKey pbk = kp.getPublic();

        String Base64PublicKey = softWrap( Base64.getEncoder().encodeToString( pbk.getEncoded() ),40 );
        String Base64PrivateKey = softWrap( Base64.getEncoder().encodeToString( pvk.getEncoded() ),40 );


        try(BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(  Files.newOutputStream(PBKFile) )) ) {
            bw.write(StartOfPrivateKey + "\n");
            bw.write(Base64PrivateKey + "\n");
            bw.write(EndOfPrivateKey + "\n");

            bw.write(StartOfPublicKey + "\n");
            bw.write(Base64PublicKey + "\n");
            bw.write(EndOfPublicKey + "\n");
            bw.flush();
        }
    }
    public static String softWrap(String input, int wrap){
        StringBuilder sb = new StringBuilder();

        int pos = 0;
        while(pos + wrap < input.length()){
            sb.append(input, pos, pos + wrap);
            sb.append("\n");
            pos += wrap;
        }
        sb.append(input,pos,input.length());
        return sb.toString();
    }
}
