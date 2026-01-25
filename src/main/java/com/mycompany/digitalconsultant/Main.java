package com.mycompany.digitalconsultant;

import com.mycompany.digitalconsultant.ai.ManusService;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Garante UTF-8 no console (Windows)
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            ManusService service = new ManusService();

            System.out.println("--------------------------------------------------");
            System.out.println("CONSULTOR DIGITAL IA - CRESCIMENTO E ESTRATÉGIA");
            System.out.println("--------------------------------------------------");
            System.out.println("Digite sua dúvida sobre marketing, vendas ou automação");
            System.out.println("Digite 'sair' para encerrar");

            while (true) {
                System.out.print("\nVocê: ");

                if (!scanner.hasNextLine()) {
                    System.out.println("\nEntrada finalizada.");
                    break;
                }

                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("sair")) {
                    System.out.println("Encerrando consultoria.");
                    break;
                }

                if (input.isEmpty()) {
                    System.out.println("Digite uma pergunta válida.");
                    continue;
                }

                String response = service.askConsultant(input);

                System.out.println("\n--- Resposta Estratégica ---");
                System.out.println(response);
                System.out.println("----------------------------");
            }

        } catch (Exception e) {
            System.err.println("Erro fatal na aplicação: " + e.getMessage());
        }
    }
}
