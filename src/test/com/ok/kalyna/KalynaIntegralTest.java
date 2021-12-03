package com.ok.kalyna;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class KalynaIntegralTest {
    @Test
    void kalynaIntegralProperty(){
        byte[] constantV = new byte[4 * 8];
        int seed = (new Random()).nextInt();
        System.out.println("Seed : " + seed);
        Random r = new Random(seed);
        r.nextBytes(constantV);
        int allIndex = r.nextInt(0,constantV.length);
        byte[][] constantValues = KalynaUtil.getState(constantV);
        System.out.println("ALL Index : " + allIndex);
        KalynaIntegral.kalynaIntegralProperty(KalynaIntegral.generateDeltaSet(constantValues, allIndex));
    }
}
