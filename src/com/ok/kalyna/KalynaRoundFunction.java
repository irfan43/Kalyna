package com.ok.kalyna;

import java.math.BigInteger;
import java.util.Arrays;

public class KalynaRoundFunction {

    public static final boolean ENCRYPTION_MODE = true;
    public static final boolean DECRYPTION_MODE = false;

    /**
     * Performs the Shift rows operation on the given state matrix
     * @param input the input state matrix
     * @return the shifted matrix
     */
    public static byte[][] shiftRows(byte[][] input){
        return shiftRows(input,ENCRYPTION_MODE);
    }
    /**
     * Performs the Inverse Shift rows operation on the given state matrix
     * @param input the input state matrix
     * @return the inverse shifted matrix
     */
    public static byte[][] invShiftRows(byte[][] input){
        return shiftRows(input,DECRYPTION_MODE);
    }
    /**
     * Performs SBox operation on the given state matrix
     * @param input the input state matrix
     * @return the substituted matrix
     */
    public static byte[][] SBox(byte[][] input){
        return substituteState(input,ENCRYPTION_MODE);
    }

    /**
     * Performs the Inverse SBox operation
     * @param input the input state matrix
     */
    public static byte[][] invSBox(byte[][] input){
        return substituteState(input,DECRYPTION_MODE);
    }

    /**
     * Performs the Mix Columns operation
     * @param input the input state matrix
     * @return the state matrix after the operation was performed
     */
    public static byte[][] mixColumns(byte[][] input){
        return MDSMultiply(input, ENCRYPTION_MODE);
    }

    /**
     * Performs the Inverse Mix Columns operation
     * @param input the input state matrix
     * @return the state matrix after the operation was performed
     */
    public static byte[][] invMixColumns(byte[][] input){
        return MDSMultiply(input, DECRYPTION_MODE);
    }

    /**
     * Performs the XOR Round key addition
     * @param input the input state matrix
     * @param roundKey the round key to add
     * @return the resultant matrix
     */
    public static byte[][] xorRoundKey(byte[][] input, byte[][] roundKey){
        return XORState(input,roundKey);
    }

    /**
     * Performs the Round Key addition on the given state matrix with mod <code>2^64</code>
     * @param input input state matrix
     * @param roundKey the round key to add
     * @return the resultant matrix
     */
    public static byte[][] addRoundKey(byte[][] input,byte[][] roundKey){
        return roundKeyMod(input,roundKey, ENCRYPTION_MODE);
    }
    /**
     * Performs the Round Key subtraction on the given state matrix with mod <code>2^64</code>
     * @param input input state matrix
     * @param roundKey the round key to subtract
     * @return the resultant matrix
     */
    public static byte[][] subRoundKey(byte[][] input,byte[][] roundKey){
        return roundKeyMod(input,roundKey, DECRYPTION_MODE);
    }

    /**
     * Performs a S-Box, Shift Rows, Mix Columns operation on the input state matrix in that order
     * and returns the resulting state matrix
     * @param input The Input state matrix on which to perform the transformation
     * @return the resulting state matrix
     */
    public static byte[][] subShiftMixTransform(byte[][] input){
        return mixColumns(shiftRows(SBox(input)));
    }
    /**
     * Performs a S-Box, Shift Rows, Mix Columns operation on the input state matrix in that order
     * and returns the resulting state matrix
     * @param input The Input state matrix on which to perform the transformation
     * @return the resulting state matrix
     */
    public static byte[][] invSubShiftMixTransform(byte[][] input){
        return invSBox(invShiftRows(invMixColumns(input)));
    }




    /**
     * performs the MDS multiply on the given input state matrix
     * @param input The state matrix
     * @param mode decryption or encryption Matrix to be used
     * @return the input matrix multiplied by MDS matrix
     */
    private static byte[][] MDSMultiply(byte[][] input, boolean mode) {

        byte[][] output = new byte[input.length][input[0].length];
        //Mix or Inverse Mix
        int offset = mode ? 0 :8 ;

        // Matrix-Column Multiply
        for(int col = 0; col < input.length; col++){
            for(int row = 0; row < input[0].length; row ++){
                for (int row1 = 0; row1 <  input[0].length ; row1++)
                    output[col][row] ^= KalynaUtil.GFLookUp[ KalynaUtil.MDSCircularVector[offset + col][row1] ][ Byte.toUnsignedInt(input[col][row1]) ];
            }
        }
        return output;
    }

    /**
     * Adds the Substitution box over the give state matrix
     * @param input the input state matrix
     * @param mode either <code>ENCRYPTION_MODE</code> or <code>DECRYPTION_MODE</code> depending on if u want inverse
     * @return the state matrix after the operation was perform
     */
    private static byte[][] substituteState(byte[][] input,boolean mode){
        byte[][] output = new byte[input.length][input[0].length];
        int offset = mode ? 0 : 4;
        for( int col = 0; col < input.length; col++){
            for(int row = 0; row < 8; row++)
                output[col][row] = KalynaUtil.SBox[offset + (row % 4)][Byte.toUnsignedInt(input[col][row])];
        }
        return output;
    }


    /**
     * Does the Mix Columns operation on the give matrix
     * @param input the matrix to do the Mix Columns operation
     * @param mode the mode, either <code>ENCRYPTION_MODE</code> or <code>DECRYPTION_MODE</code>
     * @return the state matrix after operating the mix columns
     */
    private static byte[][] shiftRows(byte[][] input, boolean mode){
        byte[][] output = new byte[input.length][input[0].length];

        for(int row = 0; row < 8; row++){
            byte[] tempRow = new byte[input.length];

            //Finding Shift Amount
            int shift = input.length * row / 8;

            //todo update to use kalyna util
            //Storing each column's row value into a temporary row
            for(int col = 0; col < input.length; col++)
                tempRow[col] = input[col][row];

            // Shifting
            tempRow = KalynaUtil.circularRotate(tempRow, mode? -1 * shift :shift ) ;

            //todo update to use kalyna util
            //Storing back into that Input State
            for(int col = 0; col < input.length; col++)
                output[col][row] = tempRow[col];

        }

        return output;
    }

    /**
     * XORs the given key over the input matrix and returns the same
     * @param input the input matrix
     * @param roundKey round key
     * @return the resulting matrix
     */
    private static byte[][] XORState(byte[][] input, byte[][] roundKey) {
        byte[][] output = new byte[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                output[i][j] = (byte) (input[i][j] ^ roundKey[i][j]);
            }
        }
        return output;
    }

    /**
     * Adds each column of the input matrix with the round key matrix with mod 2^64
     * @param input the input matrix
     * @param roundKey given round key
     * @param mode mode of operation
     * @return the resulting matrix
     */
    private static byte[][] roundKeyMod(byte[][] input, byte[][] roundKey, boolean mode){
        byte[][] output = new byte[input.length][input[0].length];
        for (int col = 0; col < input.length; col++){
            int carry = 0;
            for (int row = 0; row < input[col].length; row++) {
                int ans;
                //negate the round key val if we are decrypting
                int roundKeyVal = Byte.toUnsignedInt(roundKey[col][row]) * (mode ? 1: -1);

                ans = Byte.toUnsignedInt( input[col][row]) + roundKeyVal + carry;
                output[col][row] = (byte) ( ans & 0xFF );
                carry = ans>>8;
            }
        }
        return output;

    }
}
