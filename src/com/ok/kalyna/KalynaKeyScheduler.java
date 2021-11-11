package com.ok.kalyna;

import java.util.Arrays;

public class KalynaKeyScheduler {

    public static byte[][][] getNextRoundKey(byte[][] key,int blockNClm){


        int nRounds = getNRounds(key.length);
        byte[][][] keySchedule = new byte[nRounds][key.length][8];

        byte[][] state = getNonce(blockNClm,key.length);

        byte[][] KA = KalynaUtil.copyOf(key);
        byte[][] KW = KalynaUtil.copyOf(key);
        byte[][] tmv = new byte[key.length][8];
        for (int i = 0; i < key.length; i++) {
            for (int j = 0; j < 8; j+=2)
                tmv[i][j] = 0x01;
        }
        byte[][][] genkey = new byte[3][][];
        genkey[0] = KalynaUtil.copyOf(KA);
        genkey[1] = KalynaUtil.copyOf(KW);
        genkey[2] = KalynaUtil.copyOf(KA);


        state = scheduleRound(state,genkey);
        System.out.println("got kt - \n" + KalynaUtil.byteArrayToHex(state));



        return keySchedule;
    }
    private static byte[][] scheduleRound(byte[][] input,byte[][][] key){
        byte[][] state = KalynaUtil.copyOf(input);

        for (int i = 0; i < 3; i++) {

            if(i != 1) {
                state = KalynaRoundFunction.addRoundKey(state,key[i]);
            } else {
                state = KalynaRoundFunction.xorRoundKey(state,key[i]);
            }
            state = SRMTransform(state);
        }
        return state;
    }
    private static byte[][] SRMTransform(byte[][] input){
        byte[][] state;
        state = KalynaRoundFunction.SBox(input);
        state = KalynaRoundFunction.shiftRows(state);
        state = KalynaRoundFunction.mixColumns(state);
        return state;
    }

    /**
     * Generates the nonce for the first state of the Key Scheduler
     * @param blockNClm Number of columns in the Block state
     * @param keyNClm Number of columns in the key state
     * @return the starting nonce
     */
    private static byte[][] getNonce(int blockNClm,int keyNClm){
        byte[][] output = new byte[keyNClm][8];
        output[0][0] = (byte) ((blockNClm + keyNClm + 1) & 0xFF);
        return output;
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
        return KalynaUtil.roundCount[ keyNClm/4 ];
    }
}
