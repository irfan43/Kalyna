package com.ok.kalyna;

import java.util.Arrays;

public class KalynaKeyScheduler {

    public static byte[][][] getNextRoundKey(byte[][] key,int blockNClm){
        int nRounds = getNRounds(key.length);
        byte[][][] keySchedule = new byte[nRounds][key.length][key[0].length];

        byte[][] nonce = getNonce(blockNClm,key.length);
        byte[][] K0 = new byte[key.length][];
        for (int i = 0; i < key.length; i++) {
            K0[i] = Arrays.copyOf(key[i],key[i].length);
        }

        return keySchedule;
    }

    /**
     * Generates the nonce for the first state of the Key Scheduler
     * @param blockNClm Number of columns in the Block state
     * @param keyNClm Number of columns in the key state
     * @return the starting nonce
     */
    private static byte[][] getNonce(int blockNClm,int keyNClm){
        byte[][] output = new byte[keyNClm][8];
        output[0][1] = (byte) ((blockNClm + keyNClm + 1) & 0xFF);
        return output;
    }
    /**
     * Generates the number of rounds present
     * @param keyNClm Number of columns in the key state
     * @return the number of rounds in this mode
     */
    private static int getNRounds(int keyNClm){
        if(keyNClm != 2 && keyNClm != 4 && keyNClm != 8)
            throw new IllegalArgumentException("Invalid size for key matrix");
        return KalynaUtil.roundCount[ keyNClm/4 ];
    }
}
