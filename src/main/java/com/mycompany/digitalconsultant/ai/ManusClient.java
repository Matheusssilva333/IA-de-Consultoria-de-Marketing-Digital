package com.mycompany.digitalconsultant.ai;

import com.mycompany.digitalconsultant.config.Env;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManusClient {
    private static final String BASE_URL = "https://api.manus.ai/v1";
    private final HttpClient httpClient;
    private final String apiKey;

    public ManusClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = Env.getApiKey();
    }

    public String createTask(String goal) throws Exception {
        String json = "{\"goal\": \"" + goal.replace("\"", "\\\"") + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Falha ao criar task: " + response.body());
        }

        return extractValue(response.body(), "id");
    }

    public String createRun(String taskId) throws Exception {
        // Por padrão, a Manus pode exigir um corpo vazio ou o modelo
        String json = "{\"model\": \"manus\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + taskId + "/runs"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Falha ao criar run: " + response.body());
        }

        return extractValue(response.body(), "id");
    }

    public String getRunStatus(String runId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/runs/" + runId))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha ao obter status do run: " + response.body());
        }

        return extractValue(response.body(), "status");
    }

    public String getRunOutput(String runId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/runs/" + runId))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha ao obter output do run: " + response.body());
        }

        return extractValue(response.body(), "output");
    }

    /**
     * Extrai um valor de uma string JSON simples sem usar bibliotecas externas.
     * Funciona para chaves como "id", "status", "output".
     */
    private String extractValue(String json, String key) {
        // Regex para capturar o valor de uma chave string no JSON, lidando com aspas
        // escapadas
        // Busca por "key": "value"
        Pattern pattern = Pattern.compile("\"" + key + "\":\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String value = matcher.group(1);
            // Remove escapes básicos se necessário (ex: \" para ")
            return value.replace("\\\"", "\"").replace("\\n", "\n").replace("\\r", "\r");
        }
        return null;
    }
}
