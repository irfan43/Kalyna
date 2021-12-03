package com.ok.kalyna;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class KalynaIntegralTest {
    @Test
    void kalynaIntegralProperty(){
        byte[] constantV = new byte[4 * 8];
        int seed = (new Random()).nextInt();
        System.out.println("running with seed = " + seed);
        Random r = new Random(seed);
        r.nextBytes(constantV);
        byte[][] constantValues = KalynaUtil.getState(constantV);
        KalynaIntegral.kalynaIntegralProperty(KalynaIntegral.generateDeltaSet(constantValues, 0));
    }
}
