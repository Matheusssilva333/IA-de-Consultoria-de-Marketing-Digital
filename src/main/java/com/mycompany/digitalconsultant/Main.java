package com.mycompany.digitalconsultant;

import com.mycompany.digitalconsultant.ai.ManusService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ManusService service = new ManusService();

        System.out.println("--------------------------------------------------");
        System.out.println("CONSULTOR DIGITAL IA - CRESCIMENTO E ESTRATÉGIA");
        System.out.println("--------------------------------------------------");
        System.out.println("Digite sua dúvida sobre marketing, vendas ou automação (ou 'sair' para encerrar):");

        while (true) {
            System.out.print("\nVocê: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("sair")) {
                System.out.println("Encerrando consultoria.");
                break;
            }

            if (input.isBlank()) {
                continue;
            }

            String response = service.askConsultant(input);

            System.out.println("\n--- Resposta Estratégica ---");
            System.out.println(response);
            System.out.println("----------------------------");
        }

        scanner.close();
    }
}
