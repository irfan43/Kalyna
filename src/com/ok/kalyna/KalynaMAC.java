package com.ok.kalyna;

import java.util.Arrays;

public class KalynaMAC {

    private int             statePos;
    private byte[]          state;
    private byte[]          previousState;
    private KalynaCipher    cipher;
    private int             mode;

    public KalynaMAC(byte[] key){

        mode            = Kalyna.getMode(key.length,key.length);
        cipher          = new KalynaCipher(key,mode);

        statePos        = 0;
        state           = new byte[key.length];
        previousState   = new byte[key.length];
    }

    public void update(byte[] data){
        data = Arrays.copyOf(data,data.length);
        while (data.length > 0){
            int len = Math.min( (state.length - statePos) , data.length);
            System.arraycopy(data,0,state,statePos,len);
            statePos += len;
            data = Arrays.copyOfRange(data,len,data.length);

            if(statePos >= state.length)
                doBlock();

        }
    }

    private void doBlock() {
        previousState = cipher.EncryptBlock(XOR(previousState, state));
        statePos = 0;
    }

    public byte[] getMac(){
        byte[] last = Arrays.copyOf(state,state.length);
        for (int i = statePos; i < last.length; i++)
            last[i] = 0;
        byte[] hash = cipher.EncryptBlock(XOR(previousState, last));
        return cipher.EncryptBlock(hash);
    }

    private static byte[] XOR(byte[] A, byte[] B){
        if(A.length != B.length)
            throw new IllegalArgumentException("Length of input Array mismatched");
        byte[] output = new byte[A.length];
        for (int i = 0; i < A.length; i++)
            output[i] = (byte) (A[i] ^ B[i]);
        return output;
    }



}
