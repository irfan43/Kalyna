package com.ok.kalyna;

import java.util.Arrays;

public class KalynaHash {

    private final int     Mode;
    private final int     Size;
    private byte[]        Output;

    public KalynaHash(int size){
        switch (size){
            case 64:
            case 32:
            case 16:
                Mode = Kalyna.getMode(size,size);
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid Size of HASH given \"" + size +  "\"\nMust be 16,32, or 64");
        }
        Size = size;
        Output = new byte[0];
    }

    public void Update(byte[] data){
        Output = concatenate(Output,data);
        compressState();
    }

    public byte[] Digest(byte[] data){
        Update(data);
        return Digest();
    }
    public byte[] Digest(){
        compressState();
        return Compress(Output,Mode);
    }

    private void compressState() {
        while (Output.length >= 2*Size){
            byte[] remaining = Arrays.copyOfRange(Output, Size*2,Output.length);
            byte[] partial = Compress(Arrays.copyOf(Output,Size*2),Mode);
            Output = concatenate( partial, remaining);
        }
    }

    private byte[] concatenate(byte[] a, byte[] b){
        byte[] ab = new byte[a.length + b.length];
        System.arraycopy(a,0,ab,0,a.length);
        System.arraycopy(b,0,ab,a.length,b.length);
        return ab;
    }


    /**
     * Produces a hash of the given input data
     * @param input the input byte array
     * @param size the number of bytes in the return array (16, 32, or 64)
     * @return the hashed byte array
     * @throws IllegalArgumentException if invalid <code>size</code> is given
     */
    public static byte[] Hash(byte[] input,int size) throws IllegalArgumentException{
        return (new KalynaHash(size)).Digest(input);
    }

    /**
     * Compression algorithm used for hash function
     * @param input input to be compressed
     * @param mode the mode to be used in the block cipher
     * @return the compressed output
     * @throws IllegalArgumentException if input length is larger than the key size + block size
     */
    public static byte[] Compress(byte[] input,int mode) throws IllegalArgumentException{
        if( input.length > (Kalyna.getBlockSize(mode) + Kalyna.getKeySize(mode)))
            throw new IllegalArgumentException("input size too large for the given mode");

        byte[] paddedInput = new byte[Kalyna.getBlockSize(mode) + Kalyna.getKeySize(mode)];
        System.arraycopy(input,0,paddedInput,0,input.length);

        byte[] key  = Arrays.copyOf(paddedInput,Kalyna.getKeySize(mode));
        byte[] pt   = Arrays.copyOfRange(paddedInput,Kalyna.getKeySize(mode),Kalyna.getKeySize(mode) + Kalyna.getBlockSize(mode));

        return (new KalynaCipher(key,mode)).EncryptBlock(pt);
    }
}
