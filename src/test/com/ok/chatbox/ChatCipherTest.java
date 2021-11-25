package com.ok.chatbox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatCipherTest {

    @Test
    void softWrap() {
        System.out.println("\""  + ChatCipher.softWrap("AmpleAmpleAmpleAmpleAmpleAmpleAmpleAmple",5)+ "\"" );
    }
}