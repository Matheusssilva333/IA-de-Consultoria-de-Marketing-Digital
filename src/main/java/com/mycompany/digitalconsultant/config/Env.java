package com.mycompany.digitalconsultant.config;

public class Env {

    public static String getApiKey() {
        String key = System.getenv("MANUS_API_KEY");
        if (key == null || key.isBlank()) {
            throw new RuntimeException("MANUS_API_KEY não definida");
        }
        return key;
    }
}
