package com.ok.kalyna;

import java.util.Arrays;

public class Kalyna {


    public static byte[] sbox(byte[] input){
        byte[] output;

        return null;
    }
    public static byte[] invSbox() {
        return null;
    }

    public static byte[] mixColumns(){
        return null;
    }
    public static byte[] invMixColumns(){
        return null;
    }

    public static void encryptedBlock(){

    }
    public static void decryptBlock(){

    }

    public static void main(String[] args){
        byte[][] tst = new byte[8][8];

        for (int i = 0; i <8; i++) {
            for (int j = 0; j < 8; j++) {
                tst[i][j] = (byte) (120 + i*j);
            }
        }
        byte[] tmp = {(byte)0x00,(byte) 0xFF};
        byte[][] org = Arrays.copyOf(tst,tst.length);
        KalynaRoundFunction.SBox(tst);
        System.out.println(tst);
        KalynaRoundFunction.invSBox(tst);
        System.out.println(tst);
        if(Arrays.equals(org,tst)){
            System.out.println("PASSED");
        }else {
            System.out.println("FAILED");
        }
    }

}

