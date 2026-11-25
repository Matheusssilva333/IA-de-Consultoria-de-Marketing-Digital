package com.mycompany.digitalconsultant.excel;

import com.mycompany.digitalconsultant.bpo.BpoProcessingService;
import com.mycompany.digitalconsultant.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Integração com Excel (.xlsx) e Processamento de BPO")
public class ExcelAndBpoIntegrationTest {

    @Test
    @DisplayName("Deve gerar pasta de trabalho Excel (.xlsx) com múltiplas abas de BPO e validar integridade física")
    void testExportAndImportExcelWorkbook() {
        ExcelBpoIntegrationService excelService = new ExcelBpoIntegrationService();

        FinancialProfile profile = new FinancialProfile();
        profile.setNomeInstituicao("Asset de Investimentos Teste");
        profile.setTipo(TipoInstituicaoFinanceira.GESTORA_ASSET);
        profile.setAumOuFaturamentoMensal(new BigDecimal("35000000.00"));

        StructuredDiagnosis diag = new StructuredDiagnosis();
        diag.setDiagnostico("Diagnóstico Financeiro de Demonstração");
        diag.getAcoesPriorizadas().add(new ActionItem("Conciliação em D+1", "Automatizar extrato bancário", "ALTO", "BAIXO", "3 dias"));
        diag.getMetricas().add(new MetricTarget("Taxa de Conciliação", "100%", "Eliminação de erros de caixa"));

        List<FinancialTransactionItem> items = new ArrayList<>();
        items.add(new FinancialTransactionItem(LocalDate.now(), "RECEITA", "Taxa de Gestão", new BigDecimal("50000.00"), "Cotistas", true));
        items.add(new FinancialTransactionItem(LocalDate.now(), "DESPESA", "Taxa de Custódia B3", new BigDecimal("12000.00"), "Custodiante", true));
        items.add(new FinancialTransactionItem(LocalDate.now(), "TRIBUTO", "ISS", new BigDecimal("3500.00"), "Prefeitura", false));

        String outputPath = "./target/test-reports/Planilha_BPO_Teste.xlsx";
        File excelFile = excelService.exportFinancialWorkbook(profile, diag, items, outputPath);

        // Prova Física da Existência do Excel .xlsx
        assertTrue(excelFile.exists());
        assertTrue(excelFile.length() > 0);
        assertTrue(excelFile.getName().endsWith(".xlsx"));

        // Teste de Leitura e Reingestão da Planilha Excel
        List<FinancialTransactionItem> reimported = excelService.importFinancialWorkbook(excelFile);
        assertNotNull(reimported);
    }

    @Test
    @DisplayName("Deve calcular corretamente os indicadores operacionais de BPO Financeiro")
    void testBpoSummaryCalculations() {
        BpoProcessingService bpoService = new BpoProcessingService();

        List<FinancialTransactionItem> items = List.of(
                new FinancialTransactionItem(LocalDate.now(), "RECEITA", "Entrada A", new BigDecimal("1000.00"), "Cli 1", true),
                new FinancialTransactionItem(LocalDate.now(), "RECEITA", "Entrada B", new BigDecimal("2000.00"), "Cli 2", true),
                new FinancialTransactionItem(LocalDate.now(), "DESPESA", "Saída A", new BigDecimal("1200.00"), "Forn 1", true),
                new FinancialTransactionItem(LocalDate.now(), "DESPESA", "Saída B", new BigDecimal("300.00"), "Forn 2", false)
        );

        BpoDashboardSummary summary = bpoService.processTransactions(items);

        assertEquals(new BigDecimal("3000.00"), summary.totalReceitas());
        assertEquals(new BigDecimal("1500.00"), summary.totalDespesas());
        assertEquals(new BigDecimal("1500.00"), summary.saldoLiquido()); // 3000 - 1500
        assertEquals(new BigDecimal("300.00"), summary.totalAguardandoConciliacao());
        assertEquals(75.0, summary.taxaConciliacaoPercent()); // 3 de 4 conciliadas = 75%
    }
}
