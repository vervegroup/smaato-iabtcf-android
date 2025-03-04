package com.smaato.iabtcf.utils;

public class DecoderUtil {
    public static boolean isTestEnvironment() {
        return "true".equals(System.getProperty("IS_TEST_ENV"));
    }
}
