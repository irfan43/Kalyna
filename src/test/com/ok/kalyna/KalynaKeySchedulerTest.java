package com.ok.kalyna;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KalynaKeySchedulerTest {

    @Test
    void getNextRoundKey() {

        byte[][] key = KalynaUtil.stringToState("000102030405060708090A0B0C0D0E0F");
        KalynaKeyScheduler.getNextRoundKey(key,2);
    }
}