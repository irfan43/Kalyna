package com.ok.kalyna;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class KalynaRoundFunctionTest {

    private String[] SBoxTestCaseInput = {
            "050102030405060708090A0B0C0D0E0F",
            "62C87E6D6EBA4734E5583BDD28CACF8D",
            "53E95E9206C5D09BBF6097DCA8989844",
    };
    private String[] SBoxTestCaseOutput = {
            "75BB9A4D6BCB452A713ADFB31790511F",
            "FC4F5E9CC3232E40D98141FC1FD382BF",
            "5A04E6B66C846B1D26E724A5C50B28E5"

    };


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void sBox(){


        String[] input = SBoxTestCaseInput;
        String[] expectedOutput = SBoxTestCaseOutput;

        assertEquals(input.length,expectedOutput.length);
        for (int i = 0; i < input.length; i++) {
            byte[][] inputState = KalynaUtil.stringToState(input[i]);
            byte[][] val = KalynaUtil.stringToState(expectedOutput[i]);

            byte[][] out = KalynaRoundFunction.sBox(inputState);
            boolean pass = true;
            for (int j = 0; j < val.length; j++)
                pass = pass && Arrays.equals(val[j],out[j]);

            assertTrue(pass);
        }
    }

    @Test
    void invSBox() {
        String[] input = SBoxTestCaseOutput;
        String[] expectedOutput =  SBoxTestCaseInput;

        assertEquals(input.length,expectedOutput.length);
        for (int i = 0; i < input.length; i++) {
            byte[][] inputState = KalynaUtil.stringToState(input[i]);
            byte[][] val = KalynaUtil.stringToState(expectedOutput[i]);

            byte[][] out = KalynaRoundFunction.invSBox(inputState);
            boolean pass = true;
            for (int j = 0; j < val.length; j++)
                pass = pass && Arrays.equals(val[j],out[j]);

            assertTrue(pass);
        }
    }

    @Test
    void mixColumns() {
    }

    @Test
    void invMixColumns() {
    }



}