package com.ok.kalyna;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class KalynaCFBTest {

    @Test
    void CipherRandomLarge() {
        for (int mode = 256; mode <= Kalyna.KALYNA_512KEY_512BLOCK; mode = mode * 2){
            for (int i = 0; i < 1000; i++) {
                byte[] k = new byte[Kalyna.getKeySize(mode)];
                int len = 200 + (new Random()).nextInt(5000);
                byte[] PT = new byte[len];
                (new Random()).nextBytes(k);

                KalynaCFB kalynaEnc = new KalynaCFB(k, mode);

                byte[] CT = kalynaEnc.Update(PT);
                byte[] salt = kalynaEnc.getSALT();
                byte[] iv = kalynaEnc.getIV();

                KalynaCFB kalynaDcr = new KalynaCFB(k,mode,iv,salt);

                byte[] output = kalynaDcr.Update(CT);
                assertArrayEquals(output, PT);
            }
        }
    }
    @Test
    void CipherRandomMediumParts() {
        for (int mode = 256; mode <= Kalyna.KALYNA_512KEY_512BLOCK; mode = mode * 2){
            for (int i = 0; i < 1000; i++) {
                byte[] k = new byte[Kalyna.getKeySize(mode)];
                int len = 200 + (new Random()).nextInt(5000);
                byte[] PT = new byte[len];
                (new Random()).nextBytes(k);

                KalynaCFB kalynaEnc = new KalynaCFB(k, mode);

                byte[] CT = new byte[len];

                for (int pos = 0; pos < len;) {
                    int partLen = (new Random()).nextInt(128);
                    if( (pos + partLen) > len )
                        partLen = len - pos;

                    byte[] tmp = kalynaEnc.Update(Arrays.copyOfRange(PT,pos,pos + partLen));
                    System.arraycopy(tmp,0,CT,pos,partLen);

                    pos += partLen;
                }

                byte[] salt = kalynaEnc.getSALT();
                byte[] iv = kalynaEnc.getIV();

                KalynaCFB kalynaDcr = new KalynaCFB(k,mode,iv,salt);

                byte[] output = new byte[len];
                for (int pos = 0; pos < len;) {
                    int partLen = (new Random()).nextInt(128);
                    if( (pos + partLen) > len )
                        partLen = len - pos;

                    byte[] tmp = kalynaDcr.Update(Arrays.copyOfRange(CT,pos,pos + partLen));
                    System.arraycopy(tmp,0,output,pos,partLen);

                    pos += partLen;
                }

                assertArrayEquals(output, PT);
            }
        }
    }
}