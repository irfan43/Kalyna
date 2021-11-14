package com.ok.kalyna;

import java.util.Arrays;

public class KalynaKeyScheduler {

    public static byte[][][] generateRoundKeys(byte[][] masterKey, int numColBlock){
        int totalRounds = getNRounds(masterKey.length);
        byte[][] intermediateKey = generateIntermediateKey(masterKey, numColBlock);
        byte[][][] roundKeys = new byte[totalRounds][numColBlock][8];

        for(int round = 0; round <= totalRounds / 2; round ++){
            //Even Round Key Generation
            roundKeys[2 * round] = generateInitialRoundKeyState(masterKey, numColBlock, round);
            byte[][] roundConstantTMV = generateRoundConstantTMV(round, numColBlock);
            byte[][] intermediateConstantSum = KalynaRoundFunction.addRoundKey(roundConstantTMV, intermediateKey);

            for (int i = 0; i < 2; i++) {
                if(i ==0)
                    roundKeys[2 * round] = KalynaRoundFunction.addRoundKey(roundKeys[2 * round], intermediateConstantSum);
                else
                    roundKeys[2 * round] = KalynaRoundFunction.xorRoundKey(roundKeys[2 * round], intermediateConstantSum);

                roundKeys[2 * round] = subShiftMixTransform(roundKeys[2 * round]);
               }
            roundKeys[2 * round] = KalynaRoundFunction.addRoundKey(roundKeys[2 * round], intermediateConstantSum);
            System.out.println("ROUND " + (2 * round) + "\n" + KalynaUtil.stateToHexString(roundKeys[2 * round]));

            //Odd Round Key Generation
            if(round < totalRounds / 2){
                roundKeys[2 * round + 1] = KalynaUtil.circularRotateState(roundKeys[2 * round], 2 * numColBlock + 3);
                System.out.println("ROUND " + (2 * round + 1) + "\n" + KalynaUtil.stateToHexString(roundKeys[2 * round + 1]));
            }
        }
        return roundKeys;
    }

    private static byte[][] generateIntermediateKey(byte[][] masterKey, int numColBlock){
        //Finding the Intermediate Constant to Add to the Key
        byte[][] intermediateKey = new byte[numColBlock][8];
        intermediateKey[0][0] = (byte) ((numColBlock + masterKey.length + 1) & 0xFF);
        byte[][] keyA;
        byte[][] keyW;

        //Generating Alpha and Omega Partial Keys from Master Key
        if(numColBlock != masterKey.length){
            keyA = new byte[masterKey.length/2][8];
            keyW = new byte[masterKey.length/2][8];
            for (int col = 0; col < masterKey.length/2; col++) {
                keyA[col] = Arrays.copyOf(masterKey[col] , masterKey[col].length);
                keyW[col] = Arrays.copyOf(masterKey[masterKey.length/2 + col] , masterKey[masterKey.length/2 + col].length);
            }
        }
        else{
            keyA = KalynaUtil.copyState(masterKey);
            keyW = KalynaUtil.copyState(masterKey);
        }

        //Generating Intermediate Key
        for (int i = 0; i < 3; i++) {
            if(i != 1) {
                intermediateKey = KalynaRoundFunction.addRoundKey(intermediateKey,keyA);
            } else {
                intermediateKey = KalynaRoundFunction.xorRoundKey(intermediateKey,keyW);
            }
            intermediateKey = subShiftMixTransform(intermediateKey);
        }

        return intermediateKey;
    }

    private static byte[][] generateInitialRoundKeyState(byte[][] masterKey, int numColBlock, int round){
        byte [][] initialKeyState;
        // Same Length
        if(masterKey.length == numColBlock)
            initialKeyState = KalynaUtil.circularRotateState(masterKey, 8 * round);

            // Different Length
        else{
            initialKeyState = new byte[numColBlock][8];
            // Right Circular Rotate Master Key
            byte[][] rotatedKeyState = KalynaUtil.circularRotateState(masterKey, 8 * (round / 2));

            int offset = round % 2 == 0 ? 0 : numColBlock;
            //Initialize the Initial key State
            for (int col = 0; col < numColBlock; col++)
                System.arraycopy(rotatedKeyState[offset + col],0,initialKeyState[col],0, rotatedKeyState[col].length);
        }
        return initialKeyState;
    }

    //TODO - Find a better way for shifting after Round 16. At the moment its Hardcoded
    private static byte[][] generateRoundConstantTMV(int round, int numColRoundKey){
        // Generating Round Constant TMV value
        byte[][] roundConstantTMV = new byte[numColRoundKey][8];
        for (int col = 0; col < numColRoundKey; col++) {
            if(round < 8){
                for (int row = 0; row < 8; row += 2)
                    roundConstantTMV[col][row] = (byte) (0x01 << round);
            }
            else{
                for (int row = 1; row < 8; row += 2)
                    roundConstantTMV[col][row] = (byte) (0x01 << (round % 8));
            }
        }
        return roundConstantTMV;
    }

    private static byte[][] subShiftMixTransform(byte[][] input){
        byte[][] state;
        state = KalynaRoundFunction.SBox(input);
        state = KalynaRoundFunction.shiftRows(state);
        state = KalynaRoundFunction.mixColumns(state);
        return state;
    }

    /**
     * Generates the number of rounds present
     * @param keyNClm Number of columns in the key state
     * @return the number of rounds in this mode
     * @throws IllegalArgumentException if given invalid keyNClm
     */
    private static int getNRounds(int keyNClm) throws IllegalArgumentException{
        if(keyNClm != 2 && keyNClm != 4 && keyNClm != 8)
            throw new IllegalArgumentException("Invalid size for key matrix");
        return KalynaUtil.roundCount[ keyNClm / 4 ];
    }
}
