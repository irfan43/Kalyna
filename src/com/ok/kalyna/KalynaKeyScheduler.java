package com.ok.kalyna;

import java.util.Arrays;

public class KalynaKeyScheduler {

    public static byte[][][] getNextRoundKey(byte[][] key,int blockNClm){


        int nRounds = getNRounds(key.length);
        byte[][][] keySchedule = new byte[nRounds][key.length][8];

        byte[][] state = getNonce(blockNClm,key.length);

        byte[][] K0 = new byte[key.length][];
        byte[][] K1 = new byte[key.length][];
        byte[][] tmv = new byte[key.length][8];
//        byte[][] state = new byte[key.length][8];
        for (int i = 0; i < key.length; i++) {
            K0[i] = Arrays.copyOf(key[i],key[i].length);
            K1[i] = Arrays.copyOf(key[i],key[i].length);
//            state[i] = Arrays.copyOf(nonce[i],nonce[i].length);
            for (int j = 0; j < 8; j+=2)
                tmv[i][j] = 0x01;
        }
        System.out.println("got state - \n" + KalynaUtil.byteArrayToHex(state));
        System.out.println("got k0 - \n" + KalynaUtil.byteArrayToHex(K0));
        System.out.println("got k1 - \n" + KalynaUtil.byteArrayToHex(K1));

        for (int i = 0; i < 3; i++) {

            if(i != 1) {
                state = KalynaRoundFunction.addRoundKey(state,key);
            } else {
                state = KalynaRoundFunction.xorRoundKey(state,key);
            }
            System.out.println("Add RK - \n" + KalynaUtil.byteArrayToHex(state));

            state = KalynaRoundFunction.SBox(state);
            System.out.println("Add SBox - \n" + KalynaUtil.byteArrayToHex(state));

            state = KalynaRoundFunction.shiftRows(state);
            System.out.println("Add SRows - \n" + KalynaUtil.byteArrayToHex(state));

            state = KalynaRoundFunction.mixColumns(state);
            System.out.println("Add MC - \n" + KalynaUtil.byteArrayToHex(state));

        }
        System.out.println("got kt - \n" + KalynaUtil.byteArrayToHex(state));
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
