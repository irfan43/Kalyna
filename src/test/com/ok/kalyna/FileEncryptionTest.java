package com.ok.kalyna;

import org.junit.jupiter.api.Test;

class FileEncryptionTest {

    @Test
    void humanReadableSize() {
        System.out.println(FileEncryption.HumanReadableSize(100));
        System.out.println(FileEncryption.HumanReadableSize(1024));
        System.out.println(FileEncryption.HumanReadableSize(3234));
        System.out.println(FileEncryption.HumanReadableSize(432234234));
        System.out.println(FileEncryption.HumanReadableSize(342984));
        System.out.println(FileEncryption.HumanReadableSize(4324));
        System.out.println(FileEncryption.HumanReadableSize(4092));
    }
}