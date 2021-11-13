package com.ok.kalyna;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KalynaKeySchedulerTest {
    @Test
    void roundKeys() {
        String[] input = TestVectors.keyExpansionInput;
        String[][] expectedOutput = TestVectors.keyExpansionExpectedOutput;
        assertEquals(input.length,expectedOutput.length);

        int[] numColBlocks = {2,2,4,4,8};
        for (int state = 0; state < input.length; state++) {
            byte[][] masterKeyState = KalynaUtil.hexStringToState(input[state]);
            byte[][][] outputStates = KalynaKeyScheduler.generateRoundKeys(masterKeyState, numColBlocks[state]);
            assertEquals(outputStates.length, expectedOutput[state].length);

            for (int round = 0; round < outputStates.length; round++) {
                byte[][] expectedOutputState = KalynaUtil.hexStringToState(expectedOutput[state][round]);
                boolean pass = true;
                for (int col = 0; col < expectedOutputState.length; col++) {
                    pass = pass && Arrays.equals(expectedOutputState[col],outputStates[round][col]);
                }
                assertTrue(pass);
            }
        }
    }
}