package com.ok.kalyna;

import java.util.Arrays;

public class KalynaHash {

    public static byte[] Hash(byte[] input,int size){
        int mode;
        switch (size){
            case 512:
            case 128:
            case 256:
                mode = Kalyna.getMode(size,size);
                break;
            default:
                throw new IllegalArgumentException(" Invalid Size of HASH \nMust be 128,256, or 512");
        }

        while (size < input.length){
            byte[] part = Arrays.copyOfRange(input,0,size);
            byte[] remaining = Arrays.copyOfRange(input,size,input.length);

            byte[] tmp = Compress(part,mode);
            input = new byte[tmp.length + remaining.length];

            System.arraycopy(tmp,0,input,0,tmp.length);
            System.arraycopy(remaining,0,input,tmp.length,remaining.length);
        }

        return input;
    }
    public static byte[] Compress(byte[] input,int mode){
        if( input.length > (Kalyna.getBlockSize(mode) + Kalyna.getKeySize(mode)))
            throw new IllegalArgumentException("invalid input size for given mode");
        byte[] paddedInput = new byte[Kalyna.getBlockSize(mode) + Kalyna.getKeySize(mode)];

        System.arraycopy(input,0,paddedInput,0,input.length);

        byte[] key = Arrays.copyOf(paddedInput,Kalyna.getKeySize(mode));
        byte[] pt = Arrays.copyOfRange(paddedInput,Kalyna.getKeySize(mode),Kalyna.getKeySize(mode) + Kalyna.getBlockSize(mode));

        return (new KalynaCipher(key,mode)).EncryptBlock(pt);

    }
}
