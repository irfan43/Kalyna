package com.ok.kalyna;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class KalynaMACTest {
    @Test
    void SimpleMacs(){

        byte[] key = Arrays.copyOf( "for (int mode = 256; mode <= Kalyna.KALYNA_512KEY_512BLOCK; mode = mode * 2)".getBytes(StandardCharsets.UTF_8) ,64);

        byte[] data = new byte[550];
        (new Random()).nextBytes(data);

        KalynaMAC kmacAll = new KalynaMAC(key);
        KalynaMAC kmacParts = new KalynaMAC(key);

        kmacAll.update(data);
//        (new Random()).nextBytes(data);

        int pos = 0;
        while (pos < data.length){
            int len = Math.min( (new Random()).nextInt(16) , data.length - pos );

            byte[] part = Arrays.copyOfRange(data,pos , pos + len );
            pos += len;

            kmacParts.update(part);
        }

        byte[] mac = kmacAll.getMac();
        byte[] macSmall = kmacParts.getMac();

        System.out.println(Base64.getEncoder().encodeToString(mac));
        System.out.println(Base64.getEncoder().encodeToString(macSmall));
        assertArrayEquals(mac,macSmall);



    }

}