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
    /**
     * This class handles some ASymmetric Cryptography
     */
    private static final String StartOfPublicKey   = "---   Begin Public Key  ---";
    private static final String EndOfPublicKey     = "---    End Public Key   ---";
    private static final String StartOfPrivateKey  = "---  Begin Private Key  ---";
    private static final String EndOfPrivateKey    = "---   End Private Key   ---";

    // RSA public key
    private PublicKey   publicKey;
    // RSA private key
    private PrivateKey  privateKey;

    /**
     * Loads the keypair from the given keypair file
     * @param PBKFile input path of the keypair file
     * @throws IOException if a IOException occurs while opening PBKFile
     */
    public ChatCipher(Path PBKFile) throws IOException, NoSuchAlgorithmException {
        if(!Files.exists(PBKFile))
            GeneratePublicKey(PBKFile);
        loadKeyPair(PBKFile);
    }

    /**
     * Reads the file from path and initializes the public and private keys
     * @param PBKFile input path of the keypair file
     * @throws IOException if a IOException occurs while opening PBKFile
     */
    private void loadKeyPair(Path PBKFile) throws IOException {
        InputStream inputStream = Files.newInputStream(PBKFile);

        byte[] publicKeyEnc;
        byte[] privateKeyEnc;

        //read the text files and decode the base 64 into Byte arrays
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)) ){
            privateKeyEnc   = ReadKey(reader,StartOfPrivateKey,EndOfPrivateKey);
            publicKeyEnc    = ReadKey(reader,StartOfPublicKey,EndOfPublicKey);
        }

        //we use the JCA (Java Cryptography Architecture) to convert the binary data to key objects
        //first we get the key spec of each key
        X509EncodedKeySpec  pubKeySpec  = new X509EncodedKeySpec(publicKeyEnc);
        PKCS8EncodedKeySpec prvKeySpec  = new PKCS8EncodedKeySpec(privateKeyEnc);

        try {
            //then using the Key Factory of the JCA we get the final objects
            KeyFactory keyFactory   = KeyFactory.getInstance("RSA");
            //we pass the previously defined key specifications to the factory instance
            privateKey              = keyFactory.generatePrivate(prvKeySpec);
            publicKey               = keyFactory.generatePublic(pubKeySpec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter method for the RSA Public Key
     * @return The RSA Public Key in Base64 encoding
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString( publicKey.getEncoded() );
    }

    /**
     * Getter method for the RSA Public Key
     * @return The RSA Public Key Object
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Function to assist in Signing
     * @return A Signature object initialised with the RSA private key
     * @throws NoSuchAlgorithmException if SHA256withRSA is not supported by this JVM
     * @throws InvalidKeyException if the private key in this object is invalid
     */
    public Signature getSigning() throws NoSuchAlgorithmException, InvalidKeyException {
        Signature sgn = Signature.getInstance("SHA256withRSA");
        sgn.initSign(privateKey);
        return sgn;
    }

    /**
     * Reads lines from <code>reader</code> till it reads <code>start</code>
     * and up till it reads <code>end</code>
     * the read data is decoded using Base64
     * @param reader The BufferedReader to read from
     * @param start The Starting position to start decoding from
     * @param end The End Point to stop reading
     * @return the Decoded Binary data
     * @throws IOException if a IOException occurs while Reading from the File
     */
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

    /**
     * Generates an RSA Key Pair and stores it in the given Path in Base 64 format
     * @param PBKFile The Output File
     * @throws NoSuchAlgorithmException if the RSA algorithm is not supported by your JVM
     * @throws IOException if a IOException occurs while writing to the file
     */
    public static void GeneratePublicKey(Path PBKFile) throws NoSuchAlgorithmException, IOException {
        //we use the JCA (Java Cryptography Architecture) to generate a RSA key pair

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        //we are using 4096bit RSA private keys
        kpg.initialize(4096);
        KeyPair kp = kpg.generateKeyPair();

        PrivateKey pvk = kp.getPrivate();
        PublicKey pbk = kp.getPublic();

        //we convert the keys into Base64, this is later written into the PBKFile
        //softWrap is used here to stop long lines with no new line
        String Base64PublicKey = softWrap( Base64.getEncoder().encodeToString( pbk.getEncoded() ),40 );
        String Base64PrivateKey = softWrap( Base64.getEncoder().encodeToString( pvk.getEncoded() ),40 );

        //These Base 64 encoded Strings are written to the output file
        try(BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(  Files.newOutputStream(PBKFile) )) ) {
            //Private Key
            bw.write(StartOfPrivateKey + "\n");
            bw.write(Base64PrivateKey + "\n");
            bw.write(EndOfPrivateKey + "\n");

            //Public Key
            bw.write(StartOfPublicKey + "\n");
            bw.write(Base64PublicKey + "\n");
            bw.write(EndOfPublicKey + "\n");
            bw.flush();
        }
    }

    /**
     * Adds new lines to the string to make sure no line is longer then <code>wrap</code>
     * @param input The input String the operation is performed on
     * @param wrap The max length of a line in the output
     * @return The resulting String with lines
     */
    private static String softWrap(String input, int wrap){
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
