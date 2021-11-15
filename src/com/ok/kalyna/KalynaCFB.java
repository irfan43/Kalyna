package com.ok.kalyna;

import java.security.SecureRandom;
import java.util.Arrays;

public class KalynaCFB {

    private byte[] LastState;
    private byte[] IV;
    private byte[] SALT;
    private byte[] xorStream;

    private int StreamPos;

    private int blockSize;
    private int keySize;
    private int Mode;
    private final boolean encryption;
    private final KalynaCipher kalynaCipher;

    //TODO make a MAC implementation
    public KalynaCFB(byte[] key,int mode,byte[] iv, byte[] salt){
        encryption = false;
        SALT = Arrays.copyOf(salt,salt.length);
        IV = Arrays.copyOf(iv,iv.length);
        Mode = mode;
        setupConstants();
        kalynaCipher = new KalynaCipher(generateSaltedKey(key),mode);
        getNextStreamBlock();
    }
    public KalynaCFB(byte[] key,int mode){
        encryption = true;
        Mode = mode;
        setupConstants();
        kalynaCipher = new KalynaCipher(generateSaltedKey(key),mode);
        getNextStreamBlock();
    }

    public byte[] Update(byte[] data){
        int pos = 0;
        byte[] output = new byte[data.length];
        while (data.length > pos){
            int len = xorStream.length - StreamPos;
            if(len > data.length - pos) {
                len = data.length - pos;
            }

            byte[] tmp = updateBlock( Arrays.copyOfRange(data,pos,pos + len) );

            System.arraycopy(tmp,0,output,pos,len);
            pos += len;
        }
        return output;
    }

    public byte[] getIV(){
        return IV;
    }
    public byte[] getSALT(){
        return SALT;
    }


    private byte[] updateBlock(byte[] input){
        if(input.length > (xorStream.length - StreamPos))
            throw new IllegalArgumentException("Call of encryptBlock without sufficient Stream bytes\n" +
                    " stream remaining " + (xorStream.length - StreamPos) + " asked for " + input.length);
        if(!encryption)
            System.arraycopy(input,0,LastState,StreamPos,input.length);

        byte[] xor = Arrays.copyOfRange(xorStream,StreamPos,StreamPos + input.length);
        byte[] output = XOR(input,xor);

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
        blockSize = Kalyna.getBlockSize(Mode);
        keySize = Kalyna.getKeySize(Mode);
        if(encryption) {
            generateIV();
            generateSALT();
        }
        LastState = Arrays.copyOf(IV,IV.length);

    }
    private byte[] generateSaltedKey(byte[] key){

        return XOR(SALT,key);
    }

    private byte[] XOR(byte[] A, byte[] B){
        if(A.length != B.length)
            throw new IllegalArgumentException("Length of input Array mismatched");

        byte[] output = new byte[A.length];
        for (int i = 0; i < A.length; i++)
            output[i] = (byte) (A[i] ^ B[i]);

        return output;
    }

    private void generateSALT() {
        SALT = new byte[keySize];
        (new SecureRandom()).nextBytes(SALT);
    }

    //TODO update so first 64 bits are EPOCH to help prevent IV reuse
    private void generateIV() {
        IV = new byte[blockSize];
        (new SecureRandom()).nextBytes(IV);
    }

}
