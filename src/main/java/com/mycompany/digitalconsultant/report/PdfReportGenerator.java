package com.mycompany.digitalconsultant.report;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.mycompany.digitalconsultant.domain.model.ActionItem;
import com.mycompany.digitalconsultant.domain.model.CompanyProfile;
import com.mycompany.digitalconsultant.domain.model.MetricTarget;
import com.mycompany.digitalconsultant.domain.model.StructuredDiagnosis;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Gerador de Relatório Executivo em PDF (Conforme Fluxograma - Etapa de Geração de Relatório).
 */
public class PdfReportGenerator {

    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(15, 23, 42));
    private static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(51, 65, 85));
    private static final Font FONT_SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(30, 41, 59));
    private static final Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(51, 65, 85));
    private static final Font FONT_BODY_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(15, 23, 42));
    private static final Font FONT_FOOTER = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, new Color(148, 163, 184));

    public File generatePdfReport(CompanyProfile profile, StructuredDiagnosis diagnosis, String outputPath) {
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        try {
            File outputFile = new File(outputPath);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();

            // Cabeçalho Principal
            Paragraph title = new Paragraph("RELATÓRIO DE CONSULTORIA DE MARKETING DIGITAL", FONT_TITLE);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Diagnóstico Estratégico & Plano de Crescimento Acionável", FONT_SUBTITLE);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            // Informações da Empresa
            if (profile != null) {
                PdfPTable companyTable = new PdfPTable(2);
                companyTable.setWidthPercentage(100);
                companyTable.setSpacingAfter(15);

                PdfPCell cell1 = new PdfPCell(new Phrase("Empresa: " + profile.getNomeEmpresa(), FONT_BODY_BOLD));
                PdfPCell cell2 = new PdfPCell(new Phrase("Porte: " + profile.getPorte().getDescricao(), FONT_BODY));
                PdfPCell cell3 = new PdfPCell(new Phrase("Setor: " + profile.getSetorAtuacao(), FONT_BODY));
                PdfPCell cell4 = new PdfPCell(new Phrase("Orçamento Mensal: R$ " + profile.getOrcamentoMensal(), FONT_BODY_BOLD));

                for (PdfPCell c : new PdfPCell[]{cell1, cell2, cell3, cell4}) {
                    c.setBackgroundColor(new Color(241, 245, 249));
                    c.setPadding(6);
                    c.setBorderColor(new Color(203, 213, 225));
                    companyTable.addCell(c);
                }
                document.add(companyTable);
            }

            // Seção 1: Diagnóstico Estratégico
            document.add(new Paragraph("1. DIAGNÓSTICO ESTRATÉGICO", FONT_SECTION));
            Paragraph diagPara = new Paragraph(diagnosis.getDiagnostico(), FONT_BODY);
            diagPara.setSpacingBefore(5);
            diagPara.setSpacingAfter(15);
            document.add(diagPara);

            // Seção 2: Oportunidades Identificadas
            document.add(new Paragraph("2. OPORTUNIDADES & ALAVANCAS DE CRESCIMENTO", FONT_SECTION));
            com.lowagie.text.List list = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
            list.setListSymbol("• ");
            for (String op : diagnosis.getOportunidades()) {
                ListItem item = new ListItem(op, FONT_BODY);
                item.setSpacingAfter(4);
                list.add(item);
            }
            document.add(list);
            document.add(new Paragraph(" "));

            // Seção 3: Ações Priorizadas (Tabela)
            document.add(new Paragraph("3. PLANO DE AÇÕES PRIORIZADAS", FONT_SECTION));
            PdfPTable actionTable = new PdfPTable(new float[]{1.2f, 1.2f, 4f, 1.5f, 1.2f});
            actionTable.setWidthPercentage(100);
            actionTable.setSpacingBefore(8);
            actionTable.setSpacingAfter(15);

            String[] headers = {"Impacto", "Esforço", "Ação / Detalhes", "Prazo", "Status"};
            for (String h : headers) {
                PdfPCell head = new PdfPCell(new Phrase(h, FONT_BODY_BOLD));
                head.setBackgroundColor(new Color(226, 232, 240));
                head.setPadding(5);
                head.setBorderColor(new Color(203, 213, 225));
                actionTable.addCell(head);
            }

            for (ActionItem action : diagnosis.getAcoesPriorizadas()) {
                PdfPCell cImpacto = new PdfPCell(new Phrase(action.getImpacto(), FONT_BODY_BOLD));
                PdfPCell cEsforco = new PdfPCell(new Phrase(action.getEsforco(), FONT_BODY));
                PdfPCell cDesc = new PdfPCell(new Phrase(action.getTitulo() + "\n" + action.getDescricao(), FONT_BODY));
                PdfPCell cPrazo = new PdfPCell(new Phrase(action.getPrazoSugerido(), FONT_BODY));
                PdfPCell cStatus = new PdfPCell(new Phrase(action.isConcluida() ? "Concluída" : "Pendente", FONT_BODY));

                for (PdfPCell cell : new PdfPCell[]{cImpacto, cEsforco, cDesc, cPrazo, cStatus}) {
                    cell.setPadding(5);
                    cell.setBorderColor(new Color(226, 232, 240));
                    actionTable.addCell(cell);
                }
            }
            document.add(actionTable);

            // Seção 4: Métricas e Indicadores de Sucesso (KPIs)
            document.add(new Paragraph("4. MÉTRICAS E INDICADORES DE SUCESSO (KPIS)", FONT_SECTION));
            PdfPTable metricTable = new PdfPTable(new float[]{2.5f, 2f, 4f});
            metricTable.setWidthPercentage(100);
            metricTable.setSpacingBefore(8);
            metricTable.setSpacingAfter(20);

            for (String h : new String[]{"Métrica / KPI", "Valor Alvo", "Justificativa Estratégica"}) {
                PdfPCell head = new PdfPCell(new Phrase(h, FONT_BODY_BOLD));
                head.setBackgroundColor(new Color(226, 232, 240));
                head.setPadding(5);
                head.setBorderColor(new Color(203, 213, 225));
                metricTable.addCell(head);
            }

            for (MetricTarget m : diagnosis.getMetricas()) {
                PdfPCell cMetrica = new PdfPCell(new Phrase(m.getMetrica(), FONT_BODY_BOLD));
                PdfPCell cValor = new PdfPCell(new Phrase(m.getValorAlvo(), FONT_BODY_BOLD));
                PdfPCell cExp = new PdfPCell(new Phrase(m.getExplicacao(), FONT_BODY));

                for (PdfPCell cell : new PdfPCell[]{cMetrica, cValor, cExp}) {
                    cell.setPadding(5);
                    cell.setBorderColor(new Color(226, 232, 240));
                    metricTable.addCell(cell);
                }
            }
            document.add(metricTable);

            // Rodapé de Governança
            Paragraph footer = new Paragraph(String.format(
                    "Relatório gerado em %s | Conformidade com LGPD | FinOps Tokens: %d | Custo Est.: $%.4f USD",
                    diagnosis.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                    diagnosis.getTotalTokens(),
                    diagnosis.getCustoEstimadoUSD()
            ), FONT_FOOTER);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return outputFile;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório em PDF: " + e.getMessage(), e);
        }
    }
}
