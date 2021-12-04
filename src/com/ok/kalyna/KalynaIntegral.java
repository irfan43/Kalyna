package com.ok.kalyna;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class KalynaIntegral {

    public static byte[][][] generateDeltaSet(byte[][] constValues, int allIndex) {
        //Allocate Space for 256 Plaintext Delta Set
        byte[][][] deltaSet = new byte[256][constValues.length][constValues[0].length];
        for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
            for (int col = 0; col < deltaSet[0].length; col++) {
                for (int row = 0; row < deltaSet[0][0].length; row++) {
                    if(allIndex == col * 8 + row ){
                        deltaSet[plaintext][col][row] = (byte) (plaintext);
                        row++;
                    }else
                        deltaSet[plaintext][col][row] = constValues[col][row];
                }
            }
        }
        return deltaSet;
    }

    public static void kalynaIntegralProperty(byte[][][] deltaSet, boolean doWhitening){
        byte[] key = new byte[deltaSet[0].length * 8];
        new Random().nextBytes(key);
        byte[][] masterKey = KalynaUtil.getState(key);
        byte [][][] roundKeys = KalynaKeyScheduler.generateRoundKeys(masterKey, deltaSet[0].length);

        // Pre-Whitening Add Round Key
        if(doWhitening){
            for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
                deltaSet[plaintext] = KalynaRoundFunction.addRoundKey(deltaSet[plaintext],roundKeys[0]);
            }
            System.out.println("PRE WHITENING - ADD ROUND KEY");
            printIntegralProperty(deltaSet);
        }

        for (int round = 0; round < 3; round++) {
            // Sub Bytes
            for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
                deltaSet[plaintext] = KalynaRoundFunction.SBox(deltaSet[plaintext]);
            }
            System.out.println("ROUND " + (round + 1) +" SUB BYTES");
            printIntegralProperty(deltaSet);

            // Shift Rows
            for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
                deltaSet[plaintext] = KalynaRoundFunction.shiftRows(deltaSet[plaintext]);
            }
            System.out.println("ROUND " + (round + 1) +" SHIFT ROWS");
            printIntegralProperty(deltaSet);

            // Mix Columns
            for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
                deltaSet[plaintext] = KalynaRoundFunction.mixColumns(deltaSet[plaintext]);
            }
            System.out.println("ROUND " + (round + 1) +" MIX COLUMNS");
            printIntegralProperty(deltaSet);

            // XOR Round Key
            for (int plaintext = 0; plaintext < deltaSet.length; plaintext++) {
                deltaSet[plaintext] = KalynaRoundFunction.xorRoundKey(deltaSet[plaintext], roundKeys[round + 1]);
            }
            System.out.println("ROUND " + (round + 1) +" XOR ROUND KEY");
            printIntegralProperty(deltaSet);
        }
    }

    private static boolean checkAllProperty(Byte[] cells){
        HashSet <Byte> set = new HashSet<>(Arrays.asList(cells));
        // Unique 256 values
        return (set.size()==256);
    }

    private static boolean checkConstantProperty(Byte[] cells){
        HashSet <Byte> set = new HashSet<>(Arrays.asList(cells));
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
        System.out.println("-".repeat(1 + 4 * deltaSet[0].length));
        for (int row = 0; row < deltaSet[0][0].length; row++) {
            for (int col = 0; col < deltaSet[0].length; col++) {
                System.out.print("|");

                for (int plaintext = 0; plaintext < deltaSet.length; plaintext++)
                    cells[plaintext] = deltaSet[plaintext][col][row];

                if (checkAllProperty(cells)){
                    System.out.print(" A ");
                }else if (checkConstantProperty(cells)){
                    System.out.print(" C ");
                }else if (checkBalancedProperty(cells)){
                    System.out.print(" B ");
                }else{
                    System.out.print(" X ");
                }
            }
            System.out.println("|");
        }
        System.out.println("-".repeat(1 + 4* deltaSet[0].length));
    }

}
