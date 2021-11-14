package com.ok.kalyna;

import java.util.Arrays;

public class KalynaCipher{

    public static final int KALYNA_128KEY_128BLOCK = 256;
    public static final int KALYNA_256KEY_128BLOCK = 512;
    public static final int KALYNA_256KEY_256BLOCK = 1024;
    public static final int KALYNA_512KEY_256BLOCK = 2048;
    public static final int KALYNA_512KEY_512BLOCK = 4096;


    private byte[]      MasterKey;
    private byte[][]    MasterKeyState;
    private byte[][][]  RoundKeys;

    private int         NumberOfRounds;
    private int         ColumnsInKey;
    private int         ColumnsInPT;


    /**
     * Sets up a Kalyna Cipher object
     * @param key the master key to be used for encryption and decryption
     * @param mode defines the key size and block size
     * @throws IllegalArgumentException when given invalid key size or mode
     */
    public KalynaCipher(byte[] key, int mode) throws IllegalArgumentException{
        setupCipherState(mode);
        setKey(key);
    }

    /**
     * Takes in a mode and sets up the key size and block size
     * @param mode input mode
     * @throws IllegalArgumentException if invalid <code>mode</code> is given ie <code>mode > 4 || mode < 0</code>
     */
    private void setupCipherState(int mode) throws IllegalArgumentException {
        switch (mode){
            case KALYNA_128KEY_128BLOCK:
                NumberOfRounds = 10;
                ColumnsInKey = 2;
                ColumnsInPT = 2;
                break;
            case KALYNA_256KEY_128BLOCK:
                NumberOfRounds = 14;
                ColumnsInKey = 4;
                ColumnsInPT = 2;
                break;
            case KALYNA_256KEY_256BLOCK:
                NumberOfRounds = 14;
                ColumnsInKey = 4;
                ColumnsInPT = 4;
                break;
            case KALYNA_512KEY_256BLOCK:
                NumberOfRounds = 18;
                ColumnsInKey = 8;
                ColumnsInPT = 4;
                break;
            case KALYNA_512KEY_512BLOCK:
                NumberOfRounds = 18;
                ColumnsInKey = 8;
                ColumnsInPT = 8;
                break;
            default:
                throw new IllegalArgumentException("Invalid Mode for the Kalyna Cipher");
        }
    }

    /**
     * Sets the Key used for the Kalyna Ciphers
     * @param key The given key
     * @throws IllegalArgumentException if invalid key size is given
     */
    public void setKey(byte[] key) throws IllegalArgumentException{
        MasterKey = Arrays.copyOf(key,key.length);

        if( MasterKey.length != ColumnsInKey*8 )
            throw new IllegalArgumentException("Illegal Key Size");
        //get the state of the given key
        MasterKeyState = KalynaUtil.getState(MasterKey);
        //generate the round keys
        RoundKeys = KalynaKeyScheduler.generateRoundKeys(MasterKeyState,ColumnsInPT);
    }



    private byte[][] encryptRound(byte[][] input, byte[][] roundKey,boolean doXor){
        input = KalynaRoundFunction.subShiftMixTransform(input);
        if(doXor)
            input = KalynaRoundFunction.xorRoundKey(input, roundKey);
        return input;
    }

    private byte[][] decryptRound(byte[][] input, byte[][] roundKey,boolean doXor){
        if(doXor)
            input = KalynaRoundFunction.xorRoundKey(input, roundKey);
        input = KalynaRoundFunction.invSubShiftMixTransform((input));
        return input;
    }


    public byte[] EncryptBlock(byte [] block) throws IllegalArgumentException{
        if( block.length != ColumnsInPT * 8 )
            throw new IllegalArgumentException("Illegal Block Size");
        //get the state of the given block
        byte [][] blockState = KalynaUtil.getState(block);
        // Pre Whitening
        blockState = KalynaRoundFunction.addRoundKey(blockState,RoundKeys[0]);

        for (int round = 1; round <= NumberOfRounds; round++) {
            blockState = encryptRound(
                    blockState,
                    RoundKeys[round],
                    round != NumberOfRounds
            );
        }
        //Post Whitening
        blockState = KalynaRoundFunction.addRoundKey(blockState,RoundKeys[NumberOfRounds]);
        return KalynaUtil.reduceState(blockState);

    }

    public byte[] DecryptBlock(byte [] block) throws IllegalArgumentException{
        if( block.length != ColumnsInPT * 8 )
            throw new IllegalArgumentException("Illegal Block Size");
        //get the state of the given block
        byte [][] blockState = KalynaUtil.getState(block);
        // Undo Post Whitening
        blockState = KalynaRoundFunction.subRoundKey(blockState,RoundKeys[NumberOfRounds]);

        for (int round = NumberOfRounds; round >= 1; round--) {
            blockState = decryptRound(
                    blockState,
                    RoundKeys[round],
                    round != NumberOfRounds
            );
        }
        //Undo Pre Whitening
        blockState = KalynaRoundFunction.subRoundKey(blockState,RoundKeys[0]);
        return KalynaUtil.reduceState(blockState);

    }

    /**
     * returns the mode for the given key size and block size
     * @param BlockSize the block size in bytes
     * @param KeySize the key size in bytes
     * @return the mode of the cipher
     */
    public static int getMode(int BlockSize,int KeySize){
        if(BlockSize != 64 && BlockSize != 32 && BlockSize != 16)
            throw new IllegalArgumentException("Invalid Block Size");
        if(KeySize != 64 && KeySize != 32 && KeySize != 16)
            throw new IllegalArgumentException("Invalid Key Size");

        if( (KeySize != BlockSize) && (2 * BlockSize != KeySize) )
            throw new IllegalArgumentException("Invalid Match of Key and Block Size");

        return KeySize*BlockSize;
    }

}
