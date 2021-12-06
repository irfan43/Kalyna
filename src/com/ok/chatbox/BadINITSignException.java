package com.ok.chatbox;

import java.security.GeneralSecurityException;

/**
 * Thrown when signature of a given INIT packet has a bad signature
 */
public class BadINITSignException extends GeneralSecurityException {
    public BadINITSignException(String msg){
        super(msg);
    }
}
