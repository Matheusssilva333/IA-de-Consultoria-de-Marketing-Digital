package com.mycompany.digitalconsultant.config;

public class Env {
    public static String getApiKey() {
        String apiKey = System.getProperty("MANUS_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException(
                    "ERRO: A propriedade MANUS_API_KEY não foi configurada via VM Options (-DMANUS_API_KEY=seu_token).");
        }
        return apiKey;
    }
}
