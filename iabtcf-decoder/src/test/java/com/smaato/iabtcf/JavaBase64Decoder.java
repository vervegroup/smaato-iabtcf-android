package com.smaato.iabtcf;

import java.util.Base64;

public class JavaBase64Decoder implements Base64Decoder {
    @Override
    public byte[] decode(String input) {
        return Base64.getUrlDecoder().decode(input);
    }
}
