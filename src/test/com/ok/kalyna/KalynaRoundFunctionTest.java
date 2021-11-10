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
            "87302268407C62A8D965C73A45EFE78C",
            "090102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F",
            "81B971FDF46C29BC46EA7A2721CCE2F60C2A3CDFA70950DC16E826FDE2FC790F",
            "D9067442F75BCC4BF96A0DA25E3BF2AEB5B2E8624AD8DBE07F32B0E520FA5AA6",
            "1A1A8F1FAE700D529717DE556A8C2994001B45E76EE4FBC9B4BF888E72D5CD17"


    };
    private String[] SBoxTestCaseOutput = {
            "75BB9A4D6BCB452A713ADFB31790511F",
            "FC4F5E9CC3232E40D98141FC1FD382BF",
            "5A04E6B66C846B1D26E724A5C50B28E5",
            "467CC09BDCA48F49074589CEE4597F21",
            "DFBB9A4D6BCB452A713ADFB31790511F6D152B3DC91CBB83795C71D56F5716BD",
            "4C0C0B1B4E89A1697AC6B111DDE2134C17024CEFA03A10A52CECAD1B64C7FD1F",
            "0713146E2AD94B852579DCF847AEA042E980C515AB3968CB552E735A3E8A1807",
            "973629BDA230DCAE7043E8485B0EA129A8BA0078C3878BA3AE05C72724B36483"

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
        String[] inputState = {
                "75BB9A4D1790511F713ADFB36BCB452A"
        };
    }

    @Test
    void invMixColumns() {
    }

    @Test
    void addRoundKey(){
        String[] inputState = {
            "16CEDEE8D9990F9E25B506F042D3B305"
        };
        String[] inputKey = {
            //"000102030405060708090A0B0C0D0E0F",
            "E6865B77DCE082A0F416505E6B9B3AB1"
        };
        String[] expectedOutput =  {
            "F048859F05798D3ED1A356AE294889B4"
        };

        assertEquals(inputState.length,expectedOutput.length);
        assertEquals(inputState.length,inputKey.length);
        for (int i = 0; i < inputState.length; i++) {
            byte[][] inputStateBin = KalynaUtil.stringToState(inputState[i]);
            byte[][] val = KalynaUtil.stringToState(expectedOutput[i]);
            byte[][] out = KalynaRoundFunction.invSBox(inputStateBin);

            boolean pass = true;
            for (int j = 0; j < val.length; j++)
                pass = pass && Arrays.equals(val[j],out[j]);

            assertTrue(pass);
        }



    }


}