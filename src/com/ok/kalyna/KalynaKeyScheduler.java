package com.ok.kalyna;

public class KalynaKeyScheduler {

    public static byte[][][] GenerateRoundKeys(byte[][] key, int blockNClm){
        int nRounds = getNRounds(key.length);
        byte[][][] keySchedule = new byte[nRounds][key.length][8];

        byte[][] state = getStartingState(blockNClm,key.length);

        //TODO update for miss matched keys
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
        byte[][] id = KalynaUtil.copyOf(key);
        byte[][] kt ;//= KalynaUtil.copyOf(key);
        for (int i = 0; i < nRounds; i+=2) {
            System.out.println("ROUND " + i );
            state = scheduleRoundN(tmv,state,id);
            keySchedule[i] = KalynaUtil.copyOf(state);
            id = KalynaUtil.circularRotate(id,8);
            System.out.println("got id - \n" + KalynaUtil.byteArrayToHex(id));
            for (int j = 0; j < tmv.length; j++) {
                for (int k = 0; k < tmv[j].length; k++) {
                    tmv[j][k] = (byte) ((tmv[j][k]<<1)&0xFF);
                }
            }
            System.out.println("got tmv - \n" + KalynaUtil.byteArrayToHex(tmv));

        }
        return keySchedule;
    }
    private static byte[][] scheduleRoundN(byte[][] tmv,byte[][] state,byte[][] id){
        byte[][] kt ;//= KalynaUtil.copyOf(key);

        state = KalynaRoundFunction.addRoundKey(state,tmv);
        System.out.println("add tmv :- \n" + KalynaUtil.byteArrayToHex(state));
        kt = KalynaUtil.copyOf(state);
        state = KalynaRoundFunction.addRoundKey(state,id);
        System.out.println("add kt :- \n" + KalynaUtil.byteArrayToHex(state));
        state = SRMTransform(state);
        System.out.println("add SRM :- \n" + KalynaUtil.byteArrayToHex(state));
        state = KalynaRoundFunction.xorRoundKey(state,kt);
        //kt = KalynaUtil.copyOf(state);
        System.out.println("add XOR :- \n" + KalynaUtil.byteArrayToHex(state));

        state = SRMTransform(state);
        System.out.println("add SRM :- \n" + KalynaUtil.byteArrayToHex(state));
        state = KalynaRoundFunction.addRoundKey(state,kt);
        System.out.println("add kt :- \n" + KalynaUtil.byteArrayToHex(state));
        
        return state;
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
     * Generates the state for the first state of the Key Scheduler
     * @param blockNClm Number of columns in the Block state
     * @param keyNClm Number of columns in the key state
     * @return the starting state
     */
    private static byte[][] getStartingState(int blockNClm, int keyNClm){
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
