package com.ok.kalyna;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


class KalynaRoundFunctionTest {


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void sBox(){
        String[] input = TestVectors.SBoxTestCaseInput;
        String[] expectedOutput = TestVectors.SBoxTestCaseOutput;

        assertEquals(input.length,expectedOutput.length);
        for (int i = 0; i < input.length; i++) {
            byte[][] inputState = KalynaUtil.hexStringToState(input[i]);
            byte[][] val = KalynaUtil.hexStringToState(expectedOutput[i]);

            byte[][] out = KalynaRoundFunction.SBox(inputState);
            boolean pass = true;
            for (int j = 0; j < val.length; j++)
                pass = pass && Arrays.equals(val[j],out[j]);

            assertTrue(pass);
        }
    }

    @Test
    void invSBox() {
        String[] input = TestVectors.SBoxTestCaseOutput;
        String[] expectedOutput =  TestVectors.SBoxTestCaseInput;

        assertEquals(input.length,expectedOutput.length);
        for (int i = 0; i < input.length; i++) {
            byte[][] inputState = KalynaUtil.hexStringToState(input[i]);
            byte[][] val = KalynaUtil.hexStringToState(expectedOutput[i]);

            byte[][] out = KalynaRoundFunction.invSBox(inputState);
            boolean pass = true;
            for (int j = 0; j < val.length; j++)
                pass = pass && Arrays.equals(val[j],out[j]);

            assertTrue(pass);
        }
    }

    @Test
    void mixColumns() {
        assertEquals(TestVectors.mixColumnsInput.length, TestVectors.mixColumnsExpectedOutput.length);
        for (int state = 0; state < TestVectors.mixColumnsInput.length; state++) {
            byte[][] inputState = KalynaUtil.hexStringToState(TestVectors.mixColumnsInput[state]);
            byte[][] expectedOutputState = KalynaUtil.hexStringToState(TestVectors.mixColumnsExpectedOutput[state]);
            byte[][] outputState = KalynaRoundFunction.mixColumns(inputState);

            boolean pass = true;
            for (int col = 0; col < expectedOutputState.length; col++){
                pass = pass && Arrays.equals(expectedOutputState[col], outputState[col]);
            }
            assertTrue(pass);
        }
    }

    @Test
    void invMixColumns() {
        assertEquals(TestVectors.mixColumnsInput.length, TestVectors.mixColumnsExpectedOutput.length);
        for (int state = 0; state < TestVectors.mixColumnsInput.length; state++) {
            byte[][] inputState = KalynaUtil.hexStringToState(TestVectors.mixColumnsExpectedOutput[state]);
            byte[][] expectedOutputState = KalynaUtil.hexStringToState(TestVectors.mixColumnsInput[state]);
            byte[][] outputState = KalynaRoundFunction.invMixColumns(inputState);

            boolean pass = true;
            for (int col = 0; col < expectedOutputState.length; col++){
                pass = pass && Arrays.equals(expectedOutputState[col], outputState[col]);
            }
            assertTrue(pass);
        }
    }

    @Test
    void shiftRows() {
        assertEquals(TestVectors.shiftRowsInput.length, TestVectors.shiftRowsExpectedOutput.length);
        for (int state = 0; state < TestVectors.shiftRowsInput.length; state++) {
            byte[][] inputState = KalynaUtil.hexStringToState(TestVectors.shiftRowsInput[state]);
            byte[][] expectedOutputState = KalynaUtil.hexStringToState(TestVectors.shiftRowsExpectedOutput[state]);
            byte[][] outputState = KalynaRoundFunction.shiftRows(inputState);

            boolean pass = true;
            for (int col = 0; col < expectedOutputState.length; col++)
                pass = pass && Arrays.equals(expectedOutputState[col], outputState[col]);

            assertTrue(pass);
        }
    }

    @Test
    void invShiftRows() {
        assertEquals(TestVectors.shiftRowsInput.length, TestVectors.shiftRowsExpectedOutput.length);
        for (int state = 0; state < TestVectors.shiftRowsInput.length; state++) {
            byte[][] inputState = KalynaUtil.hexStringToState(TestVectors.shiftRowsExpectedOutput[state]);
            byte[][] expectedOutputState = KalynaUtil.hexStringToState(TestVectors.shiftRowsInput[state]);
            byte[][] outputState = KalynaRoundFunction.invShiftRows(inputState);

            boolean pass = true;
            for (int col = 0; col < expectedOutputState.length; col++)
                pass = pass && Arrays.equals(expectedOutputState[col], outputState[col]);

            assertTrue(pass);
        }
    }

    @Test
    void xorRoundKey(){
        assertEquals(TestVectors.xorRoundKeyInput.length, TestVectors.xorRoundKeyExpectedOutput.length);
        assertEquals(TestVectors.xorRoundKeyInput.length, TestVectors.xorRoundKeyInputKey.length);
        for (int state = 0; state < TestVectors.xorRoundKeyInput.length; state++) {
            byte[][] inputState = KalynaUtil.hexStringToState(TestVectors.xorRoundKeyInput[state]);
            byte[][] roundKeyState = KalynaUtil.hexStringToState(TestVectors.xorRoundKeyInputKey[state]);
            byte[][] expectedOutputState = KalynaUtil.hexStringToState(TestVectors.xorRoundKeyExpectedOutput[state]);
            byte[][] outputState = KalynaRoundFunction.xorRoundKey(inputState, roundKeyState);

            boolean pass = true;
            for (int col = 0; col < expectedOutputState.length; col++)
                pass = pass && Arrays.equals(expectedOutputState[col], outputState[col]);

            assertTrue(pass);
        }

    }

    @Test
    void addRoundKey(){
        assertEquals(TestVectors.addRoundKeyInput.length, TestVectors.addRoundKeyInputKey.length );
        assertEquals(TestVectors.addRoundKeyInput.length, TestVectors.addRoundKeyExpectedOutput.length );
        boolean pass = true;
        for (int i = 0; i < TestVectors.addRoundKeyInput.length; i++) {
            byte[][] input = KalynaUtil.hexStringToState(TestVectors.addRoundKeyInput[i]);
            byte[][] key = KalynaUtil.hexStringToState(TestVectors.addRoundKeyInputKey[i]);
            byte[][] expectedOutput = KalynaUtil.hexStringToState(TestVectors.addRoundKeyExpectedOutput[i]);

            byte[][] actualOutput = KalynaRoundFunction.addRoundKey(input,key);

            for (int j = 0; j < actualOutput.length; j++) {
                pass = pass && Arrays.equals(actualOutput[j],expectedOutput[j]);
            }
        }
        assertTrue(pass);
    }

    @Test
    void subRoundKey() {
        assertEquals(TestVectors.addRoundKeyInputKey.length, TestVectors.addRoundKeyExpectedOutput.length);
        assertEquals(TestVectors.addRoundKeyInputKey.length, TestVectors.addRoundKeyInput.length);
        for (int i = 0; i < TestVectors.addRoundKeyInput.length; i++) {
            byte[][] inputState = KalynaUtil.hexStringToState(TestVectors.addRoundKeyExpectedOutput[i]);
            byte[][] keyState = KalynaUtil.hexStringToState(TestVectors.addRoundKeyInputKey[i]);
            byte[][] expectedOutputState = KalynaUtil.hexStringToState(TestVectors.addRoundKeyInput[i]);

            byte[][] outputState = KalynaRoundFunction.subRoundKey(inputState, keyState);

            boolean pass = true;
            for (int j = 0; j < inputState.length; j++) {
                pass = pass && Arrays.equals(outputState[j], expectedOutputState[j]);
            }
            assertTrue(pass);
        }
    }
}