package com.mycompany.digitalconsultant.report;

import com.mycompany.digitalconsultant.domain.model.ActionItem;
import com.mycompany.digitalconsultant.domain.model.CompanyProfile;
import com.mycompany.digitalconsultant.domain.model.MetricTarget;
import com.mycompany.digitalconsultant.domain.model.StructuredDiagnosis;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Gerador de Relatório em Planilha CSV com formatação UTF-8 BOM para Excel e Sheets.
 */
public class SpreadsheetCsvReportGenerator {

    public File generateCsvReport(CompanyProfile profile, StructuredDiagnosis diagnosis, String outputPath) {
        File file = new File(outputPath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            // Escreve UTF-8 BOM para que o Excel abra automaticamente com acentos corretos
            writer.write('\ufeff');

            writer.println("=== RELATÓRIO DE CONSULTORIA DE MARKETING DIGITAL ===");
            if (profile != null) {
                writer.println("Empresa;" + escape(profile.getNomeEmpresa()));
                writer.println("Porte;" + escape(profile.getPorte().getDescricao()));
                writer.println("Setor;" + escape(profile.getSetorAtuacao()));
                writer.println("Orçamento Mensal (R$);" + profile.getOrcamentoMensal());
            }
            writer.println();

            writer.println("--- 1. DIAGNÓSTICO ESTRATÉGICO ---");
            writer.println("Diagnóstico;" + escape(diagnosis.getDiagnostico()));
            writer.println();

            writer.println("--- 2. OPORTUNIDADES ---");
            writer.println("Número;Oportunidade de Crescimento");
            int i = 1;
            for (String op : diagnosis.getOportunidades()) {
                writer.println(i++ + ";" + escape(op));
            }
            writer.println();

            writer.println("--- 3. PLANO DE AÇÕES PRIORIZADAS ---");
            writer.println("ID;Impacto;Esforço;Ação;Descrição;Prazo Sugerido;Status");
            for (ActionItem action : diagnosis.getAcoesPriorizadas()) {
                writer.println(String.format("%s;%s;%s;%s;%s;%s;%s",
                        escape(action.getId()),
                        escape(action.getImpacto()),
                        escape(action.getEsforco()),
                        escape(action.getTitulo()),
                        escape(action.getDescricao()),
                        escape(action.getPrazoSugerido()),
                        action.isConcluida() ? "Concluída" : "Pendente"
                ));
            }
            writer.println();

            writer.println("--- 4. MÉTRICAS E INDICADORES (KPIS) ---");
            writer.println("Métrica;Valor Alvo;Justificativa Estratégica");
            for (MetricTarget m : diagnosis.getMetricas()) {
                writer.println(String.format("%s;%s;%s",
                        escape(m.getMetrica()),
                        escape(m.getValorAlvo()),
                        escape(m.getExplicacao())
                ));
            }

            return file;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório em CSV: " + e.getMessage(), e);
        }
    }

    private String escape(String val) {
        if (val == null) return "";
        return "\"" + val.replace("\"", "\"\"").replace("\n", " ") + "\"";
    }
}
