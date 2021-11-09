package com.ok.kalyna;

import java.util.Arrays;

public class KalynaRoundFunction {

    public static final boolean ENCRYPTION_MODE = true;
    public static final boolean DECRYPTION_MODE = false;

    /**
     * Performs SBox operation on the give state matrix
     * @param input the input state matrix
     */
    public static byte[][] sBox(byte[][] input){
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
     *
     * @param input
     * @param mode
     * @return the
     */
    private static byte[][] MDSMultiply(byte[][] input, boolean mode) {
        byte[][] output = new byte[input.length][input[0].length];
        int m = mode ? 0 : 4;
        byte[] mdsRow = Arrays.copyOf(KalynaUtil.MDSCircularVector[m], KalynaUtil.MDSCircularVector[m].length);

        // Matrix-Column Multiply
        for(int col =0; col < input.length; col++){
            for(int row = 0; row < input[0].length; row ++)
                output[col][row] ^= KalynaUtil.GFLookUp[ Byte.toUnsignedInt(mdsRow[row]) ][ Byte.toUnsignedInt(input[col][row])];
            circularRotate(mdsRow,1);
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
            byte[] tempRow = new byte[8];
            
            //Finding Shift Amount
            int shift = input.length * row / 8;

            // Storing each column's row value into a temporary row
            for(int col = 0; col < input.length; col++)
                tempRow[col] = input[col][row];

            // Shifting
            tempRow = circularRotate(tempRow, mode? shift : -1 * shift) ;

            //Storing back into that Input State
            for(int col = 0; col < input.length; col++)
                output[col][row] = tempRow[col];

        }

        return output;
    }

    /**
     * Rotates right the given byte array number <code>shift</code> times
     * @param input the given byte array
     * @param shift the amount to right shift
     * @return the shifted array
     */
    private static byte[] circularRotate(byte[] input, int shift){
        shift = shift % input.length;
        byte[] output = Arrays.copyOf(input,input.length);

        System.arraycopy(input, 0, output, input.length - shift, shift);
        System.arraycopy(input, shift, output, 0, input.length - shift);
        return output;
    }

}
