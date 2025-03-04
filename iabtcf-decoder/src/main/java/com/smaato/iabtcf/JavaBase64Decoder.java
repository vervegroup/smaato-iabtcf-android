package com.smaato.iabtcf;

import java.util.Base64;

public class JavaBase64Decoder implements Base64Decoder {
    @Override
    public byte[] decode(String input) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return Base64.getUrlDecoder().decode(input); // API 26+
        } else {
            return android.util.Base64.decode(input, android.util.Base64.URL_SAFE); // API 21-25
        }
    }
}
