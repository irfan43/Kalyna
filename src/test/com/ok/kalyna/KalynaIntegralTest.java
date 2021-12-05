package com.ok.kalyna;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class KalynaIntegralTest {
    @Test
    void kalynaIntegralProperty(){
        int seed = (new Random()).nextInt();
        System.out.println("Seed : " + seed);
        Random r = new Random(seed);
        int[] numColBlock = {2, 4, 8};
        int numCols = numColBlock[r.nextInt(numColBlock.length)];
        byte[] constantV = new byte[numCols * 8];
        r.nextBytes(constantV);
        int allIndex = r.nextInt(0,constantV.length);
        boolean doWhitening = r.nextBoolean();
        byte[][] constantValues = KalynaUtil.getState(constantV);
        System.out.println("Number of Columns : " + numCols);
        System.out.println("ALL Index : " + allIndex);
        System.out.println("Pre Whitening Phase : " + doWhitening);
        KalynaIntegral.kalynaIntegralProperty(
                KalynaIntegral.generateDeltaSet(constantValues, allIndex),
                doWhitening
        );
    }
}
