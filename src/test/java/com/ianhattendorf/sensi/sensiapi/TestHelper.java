package com.ianhattendorf.sensi.sensiapi;

import java.io.InputStream;

public final class TestHelper {
    public static InputStream loadFile(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    public static boolean isNumeric(String string) {
        try {
            double d = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
