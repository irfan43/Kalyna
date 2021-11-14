package com.ok.kalyna;

import java.util.Arrays;

public class KalynaKeyScheduler {

    public static byte[][][] generateRoundKeys(byte[][] masterKey, int numColBlock){
        int totalRounds = getNRounds(masterKey.length);
        byte[][] intermediateKey = generateIntermediateKey(masterKey, numColBlock);
        byte[][][] roundKeys = new byte[totalRounds][numColBlock][8];

        for(int round = 0; round <= totalRounds / 2; round ++){
            //Even Round Key Generation
            roundKeys[2 * round] =  schedulerRound(
                        generateInitialRoundKeyState(masterKey, numColBlock, round),
                        generateIntermediateConstant(round, numColBlock,intermediateKey)
                    );

            //Odd Round Key Generation
            if(round < totalRounds / 2)
                roundKeys[2 * round + 1] = KalynaUtil.circularRotate(roundKeys[2 * round], 2 * numColBlock + 3);

        }
        return roundKeys;
    }

    private static byte[][] generateIntermediateConstant(int round,int numColBlock, byte[][] intermediateKey){
        return KalynaRoundFunction.addRoundKey(
                generateRoundConstantTMV(round, numColBlock),
                intermediateKey
        );
    }

    private static byte[][] schedulerRound(byte[][] input, byte[][] key){
        for (int i = 0; i < 2; i++) {
            if(i == 0)
                input = KalynaRoundFunction.addRoundKey(input, key);
            else
                input = KalynaRoundFunction.xorRoundKey(input, key);
            input = KalynaRoundFunction.subShiftMixTransform(input);
        }
        input = KalynaRoundFunction.addRoundKey(input , key);
        return input;
    }

    private static byte[][][] generateKeyAW(byte[][] masterKey, int numColBlock){
        byte[][][] out = new byte[2][masterKey.length/2][8];

        if(numColBlock != masterKey.length){
            for (int col = 0; col < masterKey.length/2; col++) {
                out[0][col] = Arrays.copyOf(masterKey[col] , masterKey[col].length);
                out[1][col] = Arrays.copyOf(masterKey[masterKey.length/2 + col] , masterKey[masterKey.length/2 + col].length);
            }
        }else{
            out[0] = KalynaUtil.copyOf(masterKey);
            out[1] = KalynaUtil.copyOf(masterKey);
        }

        return out;
    }
    private static byte[][] generateIntermediateKey(byte[][] masterKey, int numColBlock){
        //Finding the Intermediate Constant to Add to the Key
        byte[][] intermediateKey = new byte[numColBlock][8];
        intermediateKey[0][0] = (byte) ((numColBlock + masterKey.length + 1) & 0xFF);

        return intermediateKeyRound(
                intermediateKey,
                generateKeyAW(masterKey,numColBlock)
            );
    }

    private static byte[][] intermediateKeyRound(byte[][] intermediateKey, byte[][][] keyAW){
        for (int i = 0; i < 3; i++) {
            if(i != 1) {
                intermediateKey = KalynaRoundFunction.addRoundKey(intermediateKey,keyAW[0]);
            } else {
                intermediateKey = KalynaRoundFunction.xorRoundKey(intermediateKey,keyAW[1]);
            }
            intermediateKey = KalynaRoundFunction.subShiftMixTransform(intermediateKey);
        }
        return intermediateKey;
    }

    private static byte[][] generateInitialRoundKeyState(byte[][] masterKey, int numColBlock, int round){
        byte [][] initialKeyState;
        // Same Length
        if(masterKey.length == numColBlock)
            initialKeyState = KalynaUtil.circularRotate(masterKey, 8 * round);

        // Different Length
        else{
            initialKeyState = new byte[numColBlock][8];
            // Right Circular Rotate Master Key
            byte[][] rotatedKeyState = KalynaUtil.circularRotate(masterKey, 8 * (round/2 ));

            int offset = (round % 2 == 0) ? 0 : numColBlock;
            //Initialize the Initial key State
            for (int col = 0; col < numColBlock; col++)
                System.arraycopy(rotatedKeyState[offset + col],0,initialKeyState[col],0, rotatedKeyState[col].length);
        }
        return initialKeyState;
    }

    private static byte[][] generateRoundConstantTMV(int round, int numColRoundKey){
        // Generating Round Constant TMV value
        byte[] roundConstantTMV = new byte[numColRoundKey * 8];
        int shift = round % 8;
        for (int row = 0; row < 8 * numColRoundKey; row += 2)
            roundConstantTMV[row] = (byte) (0x01 << shift);
        return KalynaUtil.getState(
                    KalynaUtil.circularRotate( roundConstantTMV ,( -1 ) * ( round / 8 ) )
                );
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
