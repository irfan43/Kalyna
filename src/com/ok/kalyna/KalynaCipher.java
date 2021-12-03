package com.ok.kalyna;

import java.util.Arrays;

public class KalynaCipher{


    private byte[][][]  RoundKeys;

    private int         NumberOfRounds;
    private int         ColumnsInKey;
    private int         ColumnsInPT;


    /**
     * Sets up a Kalyna Cipher object
     * Used to encrypt and Decrypt Blocks using the Kalyna Cipher
     * @param key the master key to be used for encryption and decryption
     * @param mode defines the key size and block size
     * @throws IllegalArgumentException when given invalid mode or given <code>key.length</code> does not match key size for the mode
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
        ColumnsInKey    = Kalyna.getKeySize(mode)/8;
        ColumnsInPT     = Kalyna.getBlockSize(mode)/8;
        NumberOfRounds  = Kalyna.getNumberRounds(mode);


    }

    /**
     * Sets the Key used for the Kalyna Ciphers
     * @param key The given key
     * @throws IllegalArgumentException if invalid key size is given
     */
    public void setKey(byte[] key) throws IllegalArgumentException{
        byte[] masterKey = Arrays.copyOf(key, key.length);

        if( masterKey.length != ColumnsInKey*8 )
            throw new IllegalArgumentException("Illegal Key Size");
        //get the state of the given key
        byte[][] masterKeyState = KalynaUtil.getState(masterKey);
        //generate the round keys
        RoundKeys = KalynaKeyScheduler.generateRoundKeys(masterKeyState,ColumnsInPT);
    }


    /**
     * Performs a round encryption of Kalyna
     * @param input the input state on which to perform the operation
     * @param roundKey the round key to use in the round
     * @param doXor if true performs a XOR of input state with round key
     * @return the resulting state matrix
     */
    private byte[][] encryptRound(byte[][] input, byte[][] roundKey,boolean doXor){
        input = KalynaRoundFunction.subShiftMixTransform(input);
        if(doXor)
            input = KalynaRoundFunction.xorRoundKey(input, roundKey);
        return input;
    }
    /**
     * Performs a round decryption of Kalyna
     * @param input the input state on which to perform the operation
     * @param roundKey the round key to use in the round
     * @param doXor if true performs a XOR of input state with round key
     * @return the resulting state matrix
     */
    private byte[][] decryptRound(byte[][] input, byte[][] roundKey,boolean doXor){
        if(doXor)
            input = KalynaRoundFunction.xorRoundKey(input, roundKey);
        input = KalynaRoundFunction.invSubShiftMixTransform((input));
        return input;
    }

    /**
     * Encrypts a block using the Kalyna cipher
     * @param block the input block to encrypt
     * @return the encrypted block
     * @throws IllegalArgumentException if block size is not compatible with given mode
     */
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
    /**
     * Decrypts a block using the Kalyna cipher
     * @param block the input block to encrypt
     * @return the encrypted block
     * @throws IllegalArgumentException if block size is not compatible with given mode
     */
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

}
