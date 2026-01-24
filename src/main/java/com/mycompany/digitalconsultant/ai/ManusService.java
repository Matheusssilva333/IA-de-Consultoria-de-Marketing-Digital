package com.mycompany.digitalconsultant.ai;

public class ManusService {
    private final ManusClient client;

    public ManusService() {
        this.client = new ManusClient();
    }

    public String askConsultant(String prompt) {
        try {
            System.out.println("Enviando requisição estratégica para a Manus AI...");
            String taskId = client.createTask(prompt);

            System.out.println("Iniciando processamento autônomo...");
            String runId = client.createRun(taskId);

            String status = "";
            while (!"completed".equalsIgnoreCase(status) && !"failed".equalsIgnoreCase(status)) {
                Thread.sleep(2000);
                status = client.getRunStatus(runId);

                if (status == null) {
                    System.out.println("Aviso: Não foi possível obter o status. Tentando novamente...");
                    status = "";
                    continue;
                }

                System.out.print(".");
            }
            System.out.println();

            if ("failed".equalsIgnoreCase(status)) {
                return "Erro: O processamento da Manus AI falhou.";
            }

            return client.getRunOutput(runId);

        } catch (Exception e) {
            return "Erro ao processar consultoria: " + e.getMessage();
        }
    }
}
