package com.mycompany.digitalconsultant.ai;

public class ManusService {

    private final ManusClient client = new ManusClient();

    public String askConsultant(String prompt) {
        try {
            System.out.println("Enviando requisição estratégica para a Manus AI...");
            return client.ask(prompt);
        } catch (Exception e) {
            return "Erro ao processar consultoria: " + e.getMessage();
        }
    }
}
