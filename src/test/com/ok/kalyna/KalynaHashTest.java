package com.ok.kalyna;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class KalynaHashTest {

    @Test
    void hash() {
        byte[] test = "new byte[512]".getBytes(StandardCharsets.UTF_8);

        byte[] hash = KalynaHash.Hash(test,16);
        System.out.println( KalynaUtil.byteArrayToHexString(hash) );
    }
}