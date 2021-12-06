package com.ok.kalyna;

import java.util.Arrays;

public class KalynaMAC {

    private int             statePos;
    private final byte[]          state;
    private byte[]          previousState;
    private final KalynaCipher    cipher;
    private final int             mode;

    public KalynaMAC(byte[] key){

        mode            = Kalyna.getMode(key.length,key.length);
        cipher          = new KalynaCipher(key,mode);

        statePos        = 0;
        state           = new byte[key.length];
        previousState   = new byte[key.length];
    }

    public void update(byte[] data){
        // A copy is made so we do not interfere with the original data
        data = Arrays.copyOf(data,data.length);
        while (data.length > 0){
            //we move bytes from data into state
            int len = Math.min( (state.length - statePos) , data.length);
            System.arraycopy(data,0,state,statePos,len);
            statePos += len;
            data = Arrays.copyOfRange(data,len,data.length);

            //if the state is full we process it
            if(statePos >= state.length)
                processState();

        }
    }

    /**
     * Calculates the current MAC
     * @return The Calculated MAC
     * @apiNote The calculation of the MAC does not change the internal state,
     *      hence Mac can be calculated multiple times and
     *      the calculated mac corresponds to the data update at that time
     */
    public byte[] getMac(){
        byte[] last = Arrays.copyOf(state,state.length);
        for (int i = statePos; i < last.length; i++)
            last[i] = 0;
        byte[] hash = cipher.EncryptBlock(XOR(previousState, last));
        return cipher.EncryptBlock(hash);
    }

    private void processState() {
        previousState = cipher.EncryptBlock(XOR(previousState, state));
        statePos = 0;
    }

    /**
     * performs a bitwise XOR operation on the two given
     * @param A First Array to perform XOR
     * @param B Second Array to perform XOR
     * @return The Resulting Array
     * @throws IllegalArgumentException if given Arrays are of unequal Lengths
     */
    private static byte[] XOR(byte[] A, byte[] B) throws IllegalArgumentException{
        //verify the lengths are same else throw exception
        if(A.length != B.length)
            throw new IllegalArgumentException("Length of input Array mismatched");

        byte[] output = new byte[A.length];
        for (int i = 0; i < A.length; i++)
            output[i] = (byte) (A[i] ^ B[i]);
        return output;
    }



}
