package com.ok.kalyna;

import org.junit.jupiter.api.Test;

class KalynaKeySchedulerTest {

    @Test
    void getNextRoundKey() {

        byte[][] key = KalynaUtil.hexToState("000102030405060708090A0B0C0D0E0F");
        byte[][][] k = KalynaKeyScheduler.GenerateRoundKeys(key,2);
        for (int i = 0; i < k.length; i+=2) {
            System.out.println("key " + i + ":-");
            System.out.println(KalynaUtil.byteArrayToHex(k[i]));
        }
    }
}