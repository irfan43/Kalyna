package com.ok.kalyna;

import org.junit.jupiter.api.Test;

class KalynaKeySchedulerTest {

    @Test
    void getNextRoundKey() {

        byte[][] key = KalynaUtil.hexToState("000102030405060708090A0B0C0D0E0F");
        KalynaKeyScheduler.GenerateRoundKeys(key,2);
    }
}