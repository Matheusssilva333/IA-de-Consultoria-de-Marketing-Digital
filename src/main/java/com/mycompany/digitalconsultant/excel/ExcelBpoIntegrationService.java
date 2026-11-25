package com.mycompany.digitalconsultant.excel;

import com.mycompany.digitalconsultant.domain.model.ActionItem;
import com.mycompany.digitalconsultant.domain.model.FinancialProfile;
import com.mycompany.digitalconsultant.domain.model.FinancialTransactionItem;
import com.mycompany.digitalconsultant.domain.model.MetricTarget;
import com.mycompany.digitalconsultant.domain.model.StructuredDiagnosis;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de Integração com Excel (.xlsx) de Nível Industrial (Apache POI).
 * Oferece leitura de planilhas contábeis/financeiras e exportação de relatórios BPO estruturados.
 */
public class ExcelBpoIntegrationService {

    /**
     * Gera planilha Excel (.xlsx) executiva de BPO com múltiplas abas e formatação profissional.
     */
    public File exportFinancialWorkbook(FinancialProfile profile, StructuredDiagnosis diagnosis,
                                        List<FinancialTransactionItem> transactions, String outputPath) {
        File file = new File(outputPath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            CreationHelper createHelper = workbook.getCreationHelper();

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("R$ #,##0.00"));

            CellStyle boldCurrencyStyle = workbook.createCellStyle();
            boldCurrencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("R$ #,##0.00"));
            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldCurrencyStyle.setFont(boldFont);

            // ==========================================
            // ABA 1: DRE & Visão Geral
            // ==========================================
            Sheet sheetDRE = workbook.createSheet("1. DRE & Diagnostico BPO");
            int rowIdx = 0;

            Row r0 = sheetDRE.createRow(rowIdx++);
            Cell cTitle = r0.createCell(0);
            cTitle.setCellValue("PAINEL EXECUTIVO DE BPO FINANCEIRO & GESTÃO ESTRATÉGICA");
            cTitle.setCellStyle(headerStyle);

            Row rInst = sheetDRE.createRow(rowIdx++);
            rInst.createCell(0).setCellValue("Instituição:");
            rInst.createCell(1).setCellValue(profile != null ? profile.getNomeInstituicao() : "Instituição Financeira");

            Row rTipo = sheetDRE.createRow(rowIdx++);
            rTipo.createCell(0).setCellValue("Segmento:");
            rTipo.createCell(1).setCellValue(profile != null ? profile.getTipo().getDescricao() : "Mercado Financeiro");

            Row rAum = sheetDRE.createRow(rowIdx++);
            rAum.createCell(0).setCellValue("AUM / Faturamento Mensal:");
            Cell cValAum = rAum.createCell(1);
            cValAum.setCellValue(profile != null && profile.getAumOuFaturamentoMensal() != null ?
                    profile.getAumOuFaturamentoMensal().doubleValue() : 0.0);
            cValAum.setCellStyle(currencyStyle);

            rowIdx++; // Linha em branco
            Row rDiagTitle = sheetDRE.createRow(rowIdx++);
            rDiagTitle.createCell(0).setCellValue("DIAGNÓSTICO ESTRATÉGICO & LIQUIDEZ");
            rDiagTitle.getCell(0).setCellStyle(headerStyle);

            Row rDiagText = sheetDRE.createRow(rowIdx++);
            rDiagText.createCell(0).setCellValue(diagnosis != null ? diagnosis.getDiagnostico() : "Diagnóstico não executado.");

            // ==========================================
            // ABA 2: Transações & Conciliação BPO
            // ==========================================
            Sheet sheetTrans = workbook.createSheet("2. Transações e Conciliação");
            int tRowIdx = 0;

            Row rHeaderTrans = sheetTrans.createRow(tRowIdx++);
            String[] transHeaders = {"ID", "Data Competência", "Tipo/Categoria", "Descrição", "Valor (R$)", "Contraparte", "Conciliado?"};
            for (int i = 0; i < transHeaders.length; i++) {
                Cell c = rHeaderTrans.createCell(i);
                c.setCellValue(transHeaders[i]);
                c.setCellStyle(headerStyle);
            }

            if (transactions != null) {
                for (FinancialTransactionItem item : transactions) {
                    Row r = sheetTrans.createRow(tRowIdx++);
                    r.createCell(0).setCellValue(item.getId());
                    r.createCell(1).setCellValue(item.getDataCompetencia() != null ? item.getDataCompetencia().toString() : "");
                    r.createCell(2).setCellValue(item.getTipo());
                    r.createCell(3).setCellValue(item.getDescricao());

                    Cell cValor = r.createCell(4);
                    cValor.setCellValue(item.getValor() != null ? item.getValor().doubleValue() : 0.0);
                    cValor.setCellStyle(currencyStyle);

                    r.createCell(5).setCellValue(item.getContraparte());
                    r.createCell(6).setCellValue(item.isConciliado() ? "SIM (Conciliado)" : "PENDENTE");
                }
            }

            // ==========================================
            // ABA 3: Plano de Ações Financeiras
            // ==========================================
            Sheet sheetAcoes = workbook.createSheet("3. Plano de Ações Financeiras");
            int aRowIdx = 0;

            Row rHeaderAcoes = sheetAcoes.createRow(aRowIdx++);
            String[] acoesHeaders = {"Impacto em Caixa", "Esforço Operacional", "Ação de BPO / Finanças", "Descrição Prática", "Prazo", "Status"};
            for (int i = 0; i < acoesHeaders.length; i++) {
                Cell c = rHeaderAcoes.createCell(i);
                c.setCellValue(acoesHeaders[i]);
                c.setCellStyle(headerStyle);
            }

            if (diagnosis != null && diagnosis.getAcoesPriorizadas() != null) {
                for (ActionItem a : diagnosis.getAcoesPriorizadas()) {
                    Row r = sheetAcoes.createRow(aRowIdx++);
                    r.createCell(0).setCellValue(a.getImpacto());
                    r.createCell(1).setCellValue(a.getEsforco());
                    r.createCell(2).setCellValue(a.getTitulo());
                    r.createCell(3).setCellValue(a.getDescricao());
                    r.createCell(4).setCellValue(a.getPrazoSugerido());
                    r.createCell(5).setCellValue(a.isConcluida() ? "Concluída" : "Pendente");
                }
            }

            // ==========================================
            // ABA 4: KPIs & Métricas de Mercado Financeiro
            // ==========================================
            Sheet sheetKpi = workbook.createSheet("4. KPIs de Finanças e BPO");
            int kRowIdx = 0;

            Row rHeaderKpi = sheetKpi.createRow(kRowIdx++);
            String[] kpiHeaders = {"Indicador / Métrica Financeira", "Meta / Valor Alvo", "Tradução Simples para o Negócio"};
            for (int i = 0; i < kpiHeaders.length; i++) {
                Cell c = rHeaderKpi.createCell(i);
                c.setCellValue(kpiHeaders[i]);
                c.setCellStyle(headerStyle);
            }

            if (diagnosis != null && diagnosis.getMetricas() != null) {
                for (MetricTarget m : diagnosis.getMetricas()) {
                    Row r = sheetKpi.createRow(kRowIdx++);
                    r.createCell(0).setCellValue(m.getMetrica());
                    r.createCell(1).setCellValue(m.getValorAlvo());
                    r.createCell(2).setCellValue(m.getExplicacao());
                }
            }

            // Ajuste automático de largura das colunas
            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet currentSheet = workbook.getSheetAt(s);
                for (int col = 0; col < 7; col++) {
                    currentSheet.autoSizeColumn(col);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            return file;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar planilha Excel XLSX de BPO: " + e.getMessage(), e);
        }
    }

    /**
     * Importa e processa planilha Excel (.xlsx) contendo lançamentos financeiros.
     */
    public List<FinancialTransactionItem> importFinancialWorkbook(File file) {
        List<FinancialTransactionItem> items = new ArrayList<>();
        if (file == null || !file.exists()) {
            return items;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Lê primeira aba por padrão
            boolean firstRow = true;

            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false; // Pula cabeçalho
                    continue;
                }

                // Espera colunas: [0: Data/ID, 1: Tipo, 2: Descrição, 3: Valor, 4: Contraparte, 5: Conciliado]
                try {
                    Cell cTipo = row.getCell(1);
                    Cell cDesc = row.getCell(2);
                    Cell cValor = row.getCell(3);
                    Cell cContra = row.getCell(4);
                    Cell cConc = row.getCell(5);

                    String tipo = cTipo != null ? getCellValueAsString(cTipo) : "RECEITA";
                    String desc = cDesc != null ? getCellValueAsString(cDesc) : "Lançamento Contábil";
                    BigDecimal valor = BigDecimal.ZERO;

                    if (cValor != null) {
                        if (cValor.getCellType() == CellType.NUMERIC) {
                            valor = BigDecimal.valueOf(cValor.getNumericCellValue());
                        } else {
                            String vStr = getCellValueAsString(cValor).replaceAll("[^0-9,.-]", "").replace(",", ".");
                            if (!vStr.isBlank()) valor = new BigDecimal(vStr);
                        }
                    }

                    String contra = cContra != null ? getCellValueAsString(cContra) : "Banco / Fornecedor";
                    boolean conc = false;
                    if (cConc != null) {
                        String concStr = getCellValueAsString(cConc).toUpperCase();
                        conc = concStr.contains("SIM") || concStr.contains("CONCILIADO") || concStr.contains("TRUE");
                    }

                    items.add(new FinancialTransactionItem(LocalDate.now(), tipo, desc, valor, contra, conc));
                } catch (Exception rowEx) {
                    // Continua lendo outras linhas se uma linha estiver mal formatada
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar planilha Excel: " + e.getMessage(), e);
        }

        return items;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
