package com.ok.kalyna;


import java.security.PublicKey;

public class Kalyna {


    public static final int KALYNA_128KEY_128BLOCK = 256;
    public static final int KALYNA_256KEY_128BLOCK = 512;
    public static final int KALYNA_256KEY_256BLOCK = 1024;
    public static final int KALYNA_512KEY_256BLOCK = 2048;
    public static final int KALYNA_512KEY_512BLOCK = 4096;

    /**
     * returns the mode for the given key size and block size
     * @param BlockSize the block size in bytes
     * @param KeySize the key size in bytes
     * @return the mode of the cipher
     */
    public static int getMode(int BlockSize,int KeySize){
        if(
                (BlockSize  != 64 && BlockSize  != 32 && BlockSize  != 16) ||
                (KeySize    != 64 && KeySize    != 32 && KeySize    != 16)
        ) {
            if(
                    (BlockSize  != 64 && BlockSize  != 32 && BlockSize  != 16) &&
                    (KeySize    != 64 && KeySize    != 32 && KeySize    != 16)
            ) {
                throw new IllegalArgumentException("Invalid Block Size and Key Size");
            }
            if (BlockSize != 64 && BlockSize != 32 && BlockSize != 16) {
                throw new IllegalArgumentException("Invalid Block Size");
            }else{
                throw new IllegalArgumentException("Invalid Key Size");
            }
        }
        if( (KeySize != BlockSize) && (2 * BlockSize != KeySize) )
            throw new IllegalArgumentException("Invalid Match of Key and Block Size");

        return KeySize*BlockSize;
    }

    /**
     * Gives the Key Size in bytes
     * @param mode the mode in which Kalyna is running
     * @return the Key Size in bytes
     * @throws IllegalArgumentException if invalid mode is given
     */
    public static int getKeySize(int mode) throws IllegalArgumentException{
        int ColumnsInKey;
        switch (mode){
            case Kalyna.KALYNA_128KEY_128BLOCK:
                ColumnsInKey = 2;
                break;
            case Kalyna.KALYNA_256KEY_128BLOCK:
            case Kalyna.KALYNA_256KEY_256BLOCK:
                ColumnsInKey = 4;
                break;
            case Kalyna.KALYNA_512KEY_256BLOCK:
            case Kalyna.KALYNA_512KEY_512BLOCK:
                ColumnsInKey = 8;
                break;
            default:
                throw new IllegalArgumentException("Invalid Mode for the Kalyna Cipher");
        }
        return ColumnsInKey*8;
    }
    /**
     * Gives the block Size in bytes
     * @param mode the mode in which Kalyna is running
     * @return the Key Size in bytes
     * @throws IllegalArgumentException if invalid mode is given
     */
    public static int getBlockSize(int mode) throws IllegalArgumentException{
        int ColumnsInPT;
        switch (mode){
            case Kalyna.KALYNA_128KEY_128BLOCK:
            case Kalyna.KALYNA_256KEY_128BLOCK:
                ColumnsInPT = 2;
                break;
            case Kalyna.KALYNA_256KEY_256BLOCK:
            case Kalyna.KALYNA_512KEY_256BLOCK:
                ColumnsInPT = 4;
                break;
            case Kalyna.KALYNA_512KEY_512BLOCK:
                ColumnsInPT = 8;
                break;
            default:
                throw new IllegalArgumentException("Invalid Mode for the Kalyna Cipher");
        }
        return ColumnsInPT*8;
    }
    /**
     * Gives the number of rounds for a given mode
     * @param mode the mode in which Kalyna is running
     * @return the number of rounds
     * @throws IllegalArgumentException if invalid mode is given
     */
    public static int getNumberRounds(int mode) throws IllegalArgumentException{
        int NumberOfRounds;
        switch (mode){
            case Kalyna.KALYNA_128KEY_128BLOCK:
                NumberOfRounds = 10;
                break;
            case Kalyna.KALYNA_256KEY_128BLOCK:
            case Kalyna.KALYNA_256KEY_256BLOCK:
                NumberOfRounds = 14;
                break;
            case Kalyna.KALYNA_512KEY_256BLOCK:
            case Kalyna.KALYNA_512KEY_512BLOCK:
                NumberOfRounds = 18;
                break;
            default:
                throw new IllegalArgumentException("Invalid Mode for the Kalyna Cipher");
        }
        return NumberOfRounds;
    }
}

