package com.google.gson.stream;

import java.io.IOException;

public class PeekWorkaround {
    // Tiny workaround to get the token id directly, to differentiate between decimal and non decimal numbers
    public static int peekNextTokenId(JsonReader reader) {
        try {
            if(reader.peeked == 0) {
                return reader.doPeek();
            }

            return reader.peeked;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't call JsonReader.doPeek()", e);
        }
    }
}
