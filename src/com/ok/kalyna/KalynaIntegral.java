package com.ok.kalyna;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class KalynaIntegral {

    public static byte[][][] generateDeltaSet(byte[][] constValues, int allIndex) {
        //Allocate Space for 256 Plaintext Delta Set for 128/128 Kalyna
        byte[][][] deltaSet = new byte[256][2][8];
        for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
            for (int col = 0; col < deltaSet[0].length; col++) {
                for (int row = 0; row < deltaSet[0][0].length; row++) {
                    if(allIndex == col * 8 + row ){
                        deltaSet[plaintext][col][row] = (byte) (plaintext);
                        row++;
                    }
                    else
                        deltaSet[plaintext][col][row] = constValues[col][row];
                }
            }
        }
        return deltaSet;
    }

    public static void kalynaIntegralProperty(byte[][][] deltaSet){
        //Sub Bytes
        byte[][][] output = new byte[0][][];
        byte[] key = new byte[2 * 8];
        new Random().nextBytes(key);
        byte[][] masterKey = KalynaUtil.getState(key);
        byte [][][] roundKeys = KalynaKeyScheduler.generateRoundKeys(masterKey, 2);
        

        for (int round = 0; round < 3; round++) {
            // Sub Bytes
            for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
                output[plaintext] = KalynaRoundFunction.SBox(deltaSet[plaintext]);
            }
            System.out.println("ROUND  " + round + 1 +" SUB BYTES");
            printIntegralProperty(output);

            // Shift Rows
            for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
                output[plaintext] = KalynaRoundFunction.shiftRows(deltaSet[plaintext]);
            }
            System.out.println("ROUND  " + round + 1 +" SHIFT ROWS");
            printIntegralProperty(output);

            // Mix Columns
            for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
                output[plaintext] = KalynaRoundFunction.mixColumns(deltaSet[plaintext]);
            }
            System.out.println("ROUND  " + round + 1 +" MIX COLUMNS");
            printIntegralProperty(output);

            // XOR Round Key
            for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
                output[plaintext] = KalynaRoundFunction.xorRoundKey(deltaSet[plaintext], roundKeys[round]);
            }
            System.out.println("ROUND  " + round + 1 +" XOR ROUND KEY");
            printIntegralProperty(output);
        }
    }

    private static boolean checkAllProperty(Byte[] cells){
        HashSet <Byte> set   = new HashSet<Byte>(Arrays.asList(cells));
        // Unique 256 values
        return (set.size()==256);
    }

    private static boolean checkConstantProperty(Byte[] cells){
        HashSet <Byte> set   = new HashSet<Byte>(Arrays.asList(cells));
        return (set.size()==1);
    }

    private static boolean checkBalancedProperty(Byte[] cells){
        byte xorSum = 0;
        for (byte value: cells)
            xorSum ^= value;
        return xorSum == 0;
    }

    /*
        Prints Integral Property Matrix of a Delta Set
        'A' - ALL Property Cell
        'C' - CONSTANT Property Cell
        'B' - BALANCE Property Cell
        'X' - NONE Property Cell
    */
    private static void printIntegralProperty(byte[][][] deltaSet){
        Byte[] cells = new Byte[deltaSet.length];
        for (int row = 0; row < deltaSet[0][0].length; row++) {
            for (int col = 0; col < deltaSet[0].length; col++) {
                for (int plaintext = 0; plaintext < deltaSet.length; plaintext++)
                    cells[plaintext] = deltaSet[plaintext][col][row];
                if (checkAllProperty(cells)) System.out.print("A ");
                else if (checkConstantProperty(cells)) System.out.print("C ");
                else if (checkBalancedProperty(cells)) System.out.print("B ");
                else System.out.print("X ");
            }
            System.out.println();
        }
    }

}
