package com.mycompany.digitalconsultant.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.digitalconsultant.config.Env;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ManusClient {

    private static final String BASE_URL = "https://api.manus.ai/v1";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient http = HttpClient.newHttpClient();
    private final String apiKey = Env.getApiKey();

    public String ask(String prompt) throws Exception {

        ObjectNode body = MAPPER.createObjectNode();
        body.put("prompt", prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .header("API_KEY", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(
                        MAPPER.writeValueAsString(body)))
                .build();

        HttpResponse<String> response =
                http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException(
                    "HTTP " + response.statusCode() + " | " + response.body()
            );
        }

        // A resposta já vem aqui
        return response.body();
    }
}
