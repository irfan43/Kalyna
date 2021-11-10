package com.ok.kalyna;

import java.math.BigInteger;
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

    public static byte[][] xorRoundKey(byte[][] input, byte[][] roundKey){
        return XORstate(input,roundKey);
    }
    /**
     * performs the MDS mutiply
     * @param input bhla
     * @param mode bla
     * @return the
     */
    private static byte[][] MDSMultiply(byte[][] input, boolean mode) {
        byte[][] output = new byte[input.length][input[0].length];
        //Mix or Inverse Mix
        int m = mode ? 0 : 1;
        byte[] mdsRow = Arrays.copyOf(KalynaUtil.MDSCircularVector[m], KalynaUtil.MDSCircularVector[m].length);

        // Matrix-Column Multiply
        for(int col = 0; col < input.length; col++){
            for(int row = 0; row < input[0].length; row ++){
                for (int row1 = 0; row1 <  input[0].length ; row1++)
                    output[col][row] ^= KalynaUtil.GFLookUp[ mdsRow[row1] ][ Byte.toUnsignedInt(input[col][row1]) ];

                // Right Circular Rotating MDS Vector
                mdsRow = KalynaUtil.circularRotate(mdsRow,-1);
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
            byte[] tempRow = new byte[8];
            
            //Finding Shift Amount
            int shift = input.length * row / 8;

            // Storing each column's row value into a temporary row
            for(int col = 0; col < input.length; col++)
                tempRow[col] = input[col][row];

            // Shifting
            tempRow = KalynaUtil.circularRotate(tempRow, mode? shift : -1 * shift) ;

            //Storing back into that Input State
            for(int col = 0; col < input.length; col++)
                output[col][row] = tempRow[col];

        }

        return output;
    }


    private static byte[][] XORstate(byte[][] input,byte[][] roundKey) {
        byte[][] output = new byte[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                output[i][j] = (byte) (input[i][j] ^ roundKey[i][j]);
            }
        }
        return output;
    }

    private static byte[][] roundKeyMod(byte[][] input, byte[][] roundKey, boolean mode){
        byte[][] output = new byte[input.length][input[0].length];
        for (int col = 0; col < input.length; col++){
            String inputColumnHex = "00" + KalynaUtil.byteArrayToHex(input[col]);
            String roundKeyColumnHex =  "00" + KalynaUtil.byteArrayToHex(roundKey[col]);
            BigInteger tempColumn = (new BigInteger(inputColumnHex, 16));
            if(mode)
                tempColumn.add(new BigInteger(roundKeyColumnHex,16));
            else
                tempColumn.subtract(new BigInteger(roundKeyColumnHex,16));
            System.arraycopy(tempColumn.toByteArray(),0,output[col],0,output[col].length);
        }
        return output;
    }
}
