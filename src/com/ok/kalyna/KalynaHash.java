package com.ok.kalyna;

import java.util.Arrays;

public class KalynaHash {

    public static byte[] Hash(byte[] input,int size){
        int mode;
        switch (size){
            case 64:
            case 32:
            case 16:
                mode = Kalyna.getMode(size,size);
                break;
            default:
                throw new IllegalArgumentException(" Invalid Size of HASH \nMust be 16,32, or 64");
        }
        byte[] output = new byte[Math.max(size, input.length)];
        System.arraycopy(input,0,output,0,input.length);
        while (size < output.length){
            byte[] part         = Arrays.copyOfRange(output,0,size);
            byte[] remaining    = Arrays.copyOfRange(output,size,output.length);

            byte[] tmp  = Compress(part,mode);
            output      = new byte[tmp.length + remaining.length];

            System.arraycopy(tmp,0,output,0,tmp.length);
            System.arraycopy(remaining,0,output,tmp.length,remaining.length);
        }

        return output;
    }
    public static byte[] Compress(byte[] input,int mode){
        if( input.length > (Kalyna.getBlockSize(mode) + Kalyna.getKeySize(mode)))
            throw new IllegalArgumentException("invalid input size for given mode");

        byte[] paddedInput = new byte[Kalyna.getBlockSize(mode) + Kalyna.getKeySize(mode)];
        System.arraycopy(input,0,paddedInput,0,input.length);

        byte[] key  = Arrays.copyOf(paddedInput,Kalyna.getKeySize(mode));
        byte[] pt   = Arrays.copyOfRange(paddedInput,Kalyna.getKeySize(mode),Kalyna.getKeySize(mode) + Kalyna.getBlockSize(mode));

        return (new KalynaCipher(key,mode)).EncryptBlock(pt);

    }
}
