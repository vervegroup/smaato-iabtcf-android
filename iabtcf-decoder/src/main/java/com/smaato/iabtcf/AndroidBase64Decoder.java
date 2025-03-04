package com.smaato.iabtcf;

import android.util.Base64;

public class AndroidBase64Decoder implements Base64Decoder {
    @Override
    public byte[] decode(String input) {
        return Base64.decode(input, Base64.URL_SAFE);
    }
}
