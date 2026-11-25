package com.mycompany.digitalconsultant;

import Interfaceweb.Login;
import com.mycompany.digitalconsultant.ai.ManusService;
import com.mycompany.digitalconsultant.bpo.BpoProcessingService;
import com.mycompany.digitalconsultant.database.ConsultationHistoryRepository;
import com.mycompany.digitalconsultant.database.FinancialProfileRepository;
import com.mycompany.digitalconsultant.domain.model.*;
import com.mycompany.digitalconsultant.excel.ExcelBpoIntegrationService;
import com.mycompany.digitalconsultant.report.PdfReportGenerator;

import javax.swing.*;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Ponto de entrada Soberano do Consultor de MERCADO FINANCEIRO & BPO (SAO System v2.0).
 */
public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        boolean forceCli = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "--console".equalsIgnoreCase(arg)) {
                forceCli = true;
                break;
            }
        }

        if (!forceCli && !GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}
                new Login().setVisible(true);
            });
            return;
        }

        executarModoFinanceiroCLI();
    }

    private static void executarModoFinanceiroCLI() {
        System.out.println("==================================================================");
        System.out.println("   CONSULTOR INTELIGENTE - MERCADO FINANCEIRO & BPO EXECUTIVO     ");
        System.out.println("    SAO v2.0 • Gestão de AUM • BPO Financeiro • Excel XLSX • LGPD ");
        System.out.println("==================================================================");

        FinancialProfileRepository finRepo = new FinancialProfileRepository();
        ConsultationHistoryRepository historyRepo = new ConsultationHistoryRepository();
        ManusService service = new ManusService();
        BpoProcessingService bpoService = new BpoProcessingService();
        ExcelBpoIntegrationService excelService = new ExcelBpoIntegrationService();
        PdfReportGenerator pdfGen = new PdfReportGenerator();

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            System.out.println("\n[ETAPA 1] CONFIGURAÇÃO DO PERFIL FINANCEIRO & BPO (SIGILO BANCÁRIO)");
            System.out.print("Nome da Instituição / Empresa: ");
            String nomeInst = scanner.nextLine().trim();
            if (nomeInst.isEmpty()) nomeInst = "Alpha Wealth Gestão de Patrimônio";

            System.out.println("Tipo de Instituição:");
            System.out.println("1: Gestora / Asset Management | 2: Assessoria de Investimentos | 3: Family Office | 4: Empresa Cliente de BPO");
            System.out.print("Opção: ");
            String tipoOpt = scanner.nextLine().trim();
            TipoInstituicaoFinanceira tipo = switch (tipoOpt) {
                case "1" -> TipoInstituicaoFinanceira.GESTORA_ASSET;
                case "2" -> TipoInstituicaoFinanceira.ESCRITORIO_ASSESSORIA;
                case "3" -> TipoInstituicaoFinanceira.FAMILY_OFFICE;
                default -> TipoInstituicaoFinanceira.EMPRESA_CLIENTE_BPO;
            };

            System.out.print("Patrimônio sob Gestão (AUM) ou Faturamento Mensal (R$): ");
            String aumStr = scanner.nextLine().trim();
            BigDecimal aum;
            try {
                aum = new BigDecimal(aumStr);
            } catch (Exception e) {
                aum = new BigDecimal("25000000.00");
            }

            FinancialProfile profile = new FinancialProfile();
            profile.setId(UUID.randomUUID().toString());
            profile.setNomeInstituicao(nomeInst);
            profile.setTipo(tipo);
            profile.setAumOuFaturamentoMensal(aum);
            profile.setRegimeTributario("Lucro Real");
            profile.addModuloBpo(ModuloBpo.CONTAS_PAGAR_RECEBER);
            profile.addModuloBpo(ModuloBpo.CONCILIACAO_BANCARIA);
            profile.addModuloBpo(ModuloBpo.FLUXO_CAIXA_DRE);
            profile.addObjetivo(ObjetivoMercadoFinanceiro.EFICIENCIA_OPERACIONAL_BPO);
            profile.addObjetivo(ObjetivoMercadoFinanceiro.MAXIMIZACAO_LIQUIDEZ);
            profile.setConsentimentoLgpdSigilo(true);
            profile.setDataConsentimento(LocalDateTime.now());
            profile.setResponsavelFinanceiro("operador_fin_cli");

            finRepo.salvarOuAtualizar(profile);
            System.out.println("-> Perfil financeiro persistido com criptografia AES-256 e Sigilo Bancário.");

            // Lançamentos de BPO
            List<FinancialTransactionItem> items = new ArrayList<>();
            items.add(new FinancialTransactionItem(LocalDate.now(), "RECEITA", "Receita de Gestão e Performance", new BigDecimal("85000.00"), "Investidores", true));
            items.add(new FinancialTransactionItem(LocalDate.now(), "DESPESA", "Despesas Operacionais e Folha", new BigDecimal("38000.00"), "Equipe", true));
            items.add(new FinancialTransactionItem(LocalDate.now(), "TRIBUTO", "Provisão Tributária e ISS", new BigDecimal("7500.00"), "Fisco", false));

            BpoDashboardSummary bpoSummary = bpoService.processTransactions(items);
            System.out.printf("-> Resumo BPO: Receitas R$ %.2f | Despesas R$ %.2f | Saldo R$ %.2f | Conciliação: %.1f%%%n",
                    bpoSummary.totalReceitas(), bpoSummary.totalDespesas(), bpoSummary.saldoLiquido(), bpoSummary.taxaConciliacaoPercent());

            System.out.println("\n------------------------------------------------------------------");
            System.out.println("Digite sua questão financeira ou desafio de BPO (ou 'sair' para encerrar):");

            while (true) {
                System.out.print("\nExecutivo: ");
                if (!scanner.hasNextLine()) break;

                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("sair")) {
                    System.out.println("Encerrando consultoria financeira. Soberania mantida.");
                    break;
                }
                if (input.length() < 5) {
                    System.out.println("Pergunta muito curta. Detalhe o impacto em caixa ou operação.");
                    continue;
                }

                System.out.println("\n[Processando] Analisando liquidez, conciliação e traduzindo para impacto em negócios...");
                StructuredDiagnosis diagnosis = service.performFinancialConsultation(profile, input, bpoSummary);

                System.out.println("\n================== DIAGNÓSTICO FINANCEIRO & BPO ==================");
                System.out.println(diagnosis.getDiagnostico());

                System.out.println("\n================ OPORTUNIDADES DE CAIXA E LIQUIDEZ ================");
                for (String op : diagnosis.getOportunidades()) {
                    System.out.println("• " + op);
                }

                System.out.println("\n================ PLANO DE AÇÕES EM LINGUAGEM CLARA ================");
                for (ActionItem a : diagnosis.getAcoesPriorizadas()) {
                    System.out.printf("[%s | %s] %s (Prazo: %s)%n  -> %s%n",
                            a.getImpacto(), a.getEsforco(), a.getTitulo(), a.getPrazoSugerido(), a.getDescricao());
                }

                System.out.println("\n================== INDICADORES & BENCHMARKS ==================");
                for (MetricTarget m : diagnosis.getMetricas()) {
                    System.out.printf("• %s: %s (%s)%n", m.getMetrica(), m.getValorAlvo(), m.getExplicacao());
                }

                // Salva histórico
                historyRepo.salvarConsulta(new ConsultationHistoryEntry(
                        UUID.randomUUID().toString(), "operador_fin_cli", profile.getId(), input, diagnosis, LocalDateTime.now()
                ));

                // Exporta Excel e PDF
                String baseRel = "./relatorios/Relatorio_Financeiro_" + System.currentTimeMillis();
                File excelFile = excelService.exportFinancialWorkbook(profile, diagnosis, items, baseRel + ".xlsx");
                System.out.println("-> Pasta de Trabalho Excel gerada com abas de DRE, Transações e Ações em: " + excelFile.getPath());
                System.out.println("------------------------------------------------------------------");
            }

        } catch (Exception e) {
            System.err.println("Erro na execução: " + e.getMessage());
        }
    }
}
