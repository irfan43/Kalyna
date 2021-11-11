package com.ok.kalyna;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KalynaUtilTest {

    @Test
    void getState() {
    }

    @Test
    void expandState() {
    }

    @Test
    void shiftLeft() {
        String in = "00000000010000000001";
        byte[] out = KalynaUtil.shiftLeft(KalynaUtil.hexToByteArray(in) , 1);
        System.out.println("got :-\n" + KalynaUtil.byteArrayToHex(out));
    }

    @Test
    void shiftRight() {
    }

    @Test
    void circularRotate() {
    }

    @Test
    void testCircularRotate() {
    }
}