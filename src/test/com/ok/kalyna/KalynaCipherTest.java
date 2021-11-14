package com.ok.kalyna;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class KalynaCipherTest {

    @Test
    void EncryptBlock() {
        assertEquals(TestVectors.BlockEncryptionInput.length, TestVectors.BlockEncryptionOutput.length);
        assertEquals(TestVectors.BlockEncryptionInput.length, TestVectors.BlockEncryptionKey.length);
        boolean pass = true;

        for (int state = 0; state < TestVectors.BlockEncryptionInput.length; state++) {
            byte[] input = KalynaUtil.reduceState(KalynaUtil.hexStringToState(TestVectors.BlockEncryptionInput[state]));
            byte[] expectedOutput =  KalynaUtil.reduceState(KalynaUtil.hexStringToState(TestVectors.BlockEncryptionOutput[state]));
            byte[] key = KalynaUtil.reduceState(KalynaUtil.hexStringToState(TestVectors.BlockEncryptionKey[state]));
            byte[] output = new KalynaCipher(key, KalynaCipher.getMode(input.length, key.length)).EncryptBlock(input);

            pass = pass && Arrays.equals(expectedOutput, output);

        }
        assertTrue(pass);
    }

    @Test
    void DecryptBlock() {
        assertEquals(TestVectors.BlockEncryptionInput.length, TestVectors.BlockEncryptionOutput.length);
        assertEquals(TestVectors.BlockEncryptionInput.length, TestVectors.BlockEncryptionKey.length);
        boolean pass = true;

        for (int state = 0; state < TestVectors.BlockEncryptionOutput.length; state++) {
            byte[] input = KalynaUtil.reduceState(KalynaUtil.hexStringToState(TestVectors.BlockEncryptionOutput[state]));
            byte[] expectedOutput =  KalynaUtil.reduceState(KalynaUtil.hexStringToState(TestVectors.BlockEncryptionInput[state]));
            byte[] key = KalynaUtil.reduceState(KalynaUtil.hexStringToState(TestVectors.BlockEncryptionKey[state]));
            byte[] output = new KalynaCipher(key, KalynaCipher.getMode(input.length, key.length)).DecryptBlock(input);

            pass = pass && Arrays.equals(expectedOutput, output);

        }
        assertTrue(pass);
    }
}