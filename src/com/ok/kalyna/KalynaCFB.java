package com.ok.kalyna;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

public class KalynaCFB {

    private byte[]              IV;
    private byte[]              SALT;
    private byte[]              LastState;
    private byte[]              xorStream;

    private int                 StreamPos;

    private int                 blockSize;
    private int                 keySize;
    private final int           Mode;
    private final boolean       encryption;
    private final KalynaCipher  kalynaCipher;

    //TODO make a MAC implementation
    //Decryption
    public KalynaCFB(byte[] key,int mode,byte[] iv, byte[] salt){
        if(salt.length != Kalyna.getKeySize(mode))
            throw new IllegalArgumentException("Invalid Size of salt given");
        if( iv.length != Kalyna.getBlockSize(mode))
            throw new IllegalArgumentException("Invalid size of iv given");

        encryption  = false;
        SALT        = Arrays.copyOf(salt,salt.length);
        IV          = Arrays.copyOf(iv,iv.length);
        Mode        = mode;

        setupConstants();

        kalynaCipher = new KalynaCipher(generateSaltedKey(key),mode);
        getNextStreamBlock();
    }
    //Encryption
    public KalynaCFB(byte[] key,int mode){
        encryption = true;
        Mode = mode;
        setupConstants();
        kalynaCipher = new KalynaCipher(generateSaltedKey(key),mode);
        getNextStreamBlock();
    }

    public byte[] getSALT(){
        return SALT;
    }
    public byte[] getIV(){
        return IV;
    }

    public byte[] Update(byte[] data){
        int pos         = 0;
        byte[] output   = new byte[data.length];

        while (data.length > pos){
            int len     = Math.min(
                    ( xorStream.length - StreamPos), //remaining bytes in XOR stream
                    ( data.length - pos )            //remaining bytes of plaintext to encrypt
            );

            byte[] partial  = updateBlock( Arrays.copyOfRange(data,pos,pos + len) );

            System.arraycopy(partial,0,output,pos,len);
            pos += len;
        }
        return output;
    }

    /**
     * performs encryption or decryption of the given input array
     * @param input the input to encrypt or decrypt
     * @return the processed input
     * @throws IllegalArgumentException if number of bytes in input exceeds the available key stream
     */
    private byte[] updateBlock(byte[] input) throws IllegalArgumentException{
        if(input.length > (xorStream.length - StreamPos))
            throw new IllegalArgumentException("Call of encryptBlock without sufficient Stream bytes\n" +
                    " stream remaining " + (xorStream.length - StreamPos) + " asked for " + input.length);

        if(!encryption)
            System.arraycopy(input,0,LastState,StreamPos,input.length);

        byte[] xor      = Arrays.copyOfRange(xorStream,StreamPos,StreamPos + input.length);
        byte[] output   = XOR(input,xor);

        if(encryption)
            System.arraycopy(output,0,LastState,StreamPos,output.length);

        StreamPos += output.length;

        if(StreamPos >= xorStream.length)
            getNextStreamBlock();
        return output;
    }
    private void getNextStreamBlock(){
        LastState = kalynaCipher.EncryptBlock(LastState);
        xorStream = Arrays.copyOf(LastState,LastState.length);
        StreamPos = 0;
    }
    private void setupConstants(){
        blockSize   = Kalyna.getBlockSize(Mode);
        keySize     = Kalyna.getKeySize(Mode);

        if(encryption) {
            generateIV();
            generateSALT();
        }
        LastState   = Arrays.copyOf(IV,IV.length);

    }
    private byte[] generateSaltedKey(byte[] key){
        return XOR(SALT,key);
    }

    private static byte[] XOR(byte[] A, byte[] B){
        if(A.length != B.length)
            throw new IllegalArgumentException("Length of input Array mismatched");

        byte[] output = new byte[A.length];
        for (int i = 0; i < A.length; i++)
            output[i] = (byte) (A[i] ^ B[i]);

        return output;
    }
    //salt is used to XOR with the key
    private void generateSALT() {
        SALT = new byte[keySize];
        (new SecureRandom()).nextBytes(SALT);
    }
    //iv is used as the initial block
    private void generateIV() {
        IV = new byte[blockSize];
        (new SecureRandom()).nextBytes(IV);
        //the first 64bits are epoch to prevent repeating of IV
        byte[] epoch = ByteBuffer.allocate(8).putLong(System.currentTimeMillis()).array();
        System.arraycopy(epoch,0,IV,0,8);
    }

}
