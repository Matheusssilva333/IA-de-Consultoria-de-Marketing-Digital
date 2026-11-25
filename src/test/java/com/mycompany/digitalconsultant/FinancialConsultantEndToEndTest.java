package com.mycompany.digitalconsultant;

import com.mycompany.digitalconsultant.ai.ManusService;
import com.mycompany.digitalconsultant.bpo.BpoProcessingService;
import com.mycompany.digitalconsultant.database.ConsultationHistoryRepository;
import com.mycompany.digitalconsultant.database.DatabaseManager;
import com.mycompany.digitalconsultant.database.FinancialProfileRepository;
import com.mycompany.digitalconsultant.database.UserRepository;
import com.mycompany.digitalconsultant.datamesh.domain.BpoOperationsProduct;
import com.mycompany.digitalconsultant.datamesh.domain.FinancialProfileProduct;
import com.mycompany.digitalconsultant.datamesh.governance.DataMeshFederationRegistry;
import com.mycompany.digitalconsultant.domain.model.*;
import com.mycompany.digitalconsultant.excel.ExcelBpoIntegrationService;
import com.mycompany.digitalconsultant.security.PasswordSecurity;
import com.mycompany.digitalconsultant.security.SessionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste End-to-End (E2E) - Jornada Completa do Mercado Financeiro & BPO")
public class FinancialConsultantEndToEndTest {

    @BeforeAll
    static void setup() {
        DatabaseManager.getInstance().configureInMemoryMode("fin_e2e_db");
    }

    @Test
    @DisplayName("Jornada E2E Mercado Financeiro: Login -> Sigilo Bancário -> BPO -> RAG Financeiro -> IA com Tradução Simples -> Data Mesh -> Excel XLSX")
    void testFinancialConsultantCompleteEndToEndFlow() {
        // 1. AUTENTICAÇÃO SEGURA (OWASP & SESSION LOCK)
        UserRepository userRepo = new UserRepository();
        SessionManager sessionManager = SessionManager.getInstance();

        String finUser = "gestor_alpha";
        String finPass = "Wealth@2026#Secure";
        String salt = PasswordSecurity.generateSalt();
        String hash = PasswordSecurity.hashPassword(finPass, salt);

        userRepo.salvar(new UserAccount(finUser, hash, salt, "Diretor de Investimentos", "diretoria@alphagestao.com"));
        assertTrue(PasswordSecurity.verifyPassword(finPass, hash, salt));
        String token = sessionManager.createSession(finUser);
        assertTrue(sessionManager.isValidSession(token));

        // 2. CADASTRO DE PERFIL DE GESTORA / WEALTH COM CRIPTOGRAFIA AES-256 E SIGILO BANCÁRIO
        FinancialProfileRepository finRepo = new FinancialProfileRepository();
        FinancialProfile profile = new FinancialProfile();
        profile.setId(UUID.randomUUID().toString());
        profile.setNomeInstituicao("Alpha Wealth & Asset Management");
        profile.setTipo(TipoInstituicaoFinanceira.GESTORA_ASSET);
        profile.setAumOuFaturamentoMensal(new BigDecimal("60000000.00")); // R$ 60 Milhões sob Gestão
        profile.setRegimeTributario("Lucro Real");
        profile.setModulosBpoAtivos(Set.of(ModuloBpo.CONTAS_PAGAR_RECEBER, ModuloBpo.CONCILIACAO_BANCARIA, ModuloBpo.FLUXO_CAIXA_DRE));
        profile.setObjetivos(Set.of(ObjetivoMercadoFinanceiro.AUMENTO_AUM, ObjetivoMercadoFinanceiro.MAXIMIZACAO_LIQUIDEZ));
        profile.setConsentimentoLgpdSigilo(true);
        profile.setResponsavelFinanceiro(finUser);

        finRepo.salvarOuAtualizar(profile);

        // Prova de recuperação correta e segura
        FinancialProfile saved = finRepo.buscarPorId(profile.getId()).orElseThrow();
        assertEquals(new BigDecimal("60000000.00"), saved.getAumOuFaturamentoMensal());
        assertTrue(saved.isConsentimentoLgpdSigilo());

        // 3. OPERAÇÃO DE BPO FINANCEIRO (LANÇAMENTOS & CONCILIAÇÃO)
        BpoProcessingService bpoService = new BpoProcessingService();
        List<FinancialTransactionItem> items = new ArrayList<>();
        items.add(new FinancialTransactionItem(LocalDate.now(), "RECEITA", "Taxa de Administração Fundo DI", new BigDecimal("180000.00"), "Custódia B3", true));
        items.add(new FinancialTransactionItem(LocalDate.now(), "RECEITA", "Taxa de Performance Fundo Multimercado", new BigDecimal("75000.00"), "Investidores", true));
        items.add(new FinancialTransactionItem(LocalDate.now(), "DESPESA", "Despesas Operacionais e Folha BPO", new BigDecimal("65000.00"), "Equipe", true));
        items.add(new FinancialTransactionItem(LocalDate.now(), "TRIBUTO", "Provisão de PIS/COFINS e ISS", new BigDecimal("22500.00"), "Receita", false));

        BpoDashboardSummary bpoSummary = bpoService.processTransactions(items);
        assertEquals(new BigDecimal("255000.00"), bpoSummary.totalReceitas());
        assertEquals(new BigDecimal("87500.00"), bpoSummary.totalDespesas());
        assertEquals(new BigDecimal("167500.00"), bpoSummary.saldoLiquido()); // 255k - 87.5k
        assertEquals(75.0, bpoSummary.taxaConciliacaoPercent());

        // 4. CONSULTORIA COM IA, RAG FINANCEIRO E TRADUÇÃO SIMPLES PARA NEGÓCIOS
        ManusService manusService = new ManusService();
        String questaoExecutiva = "Temos R$ 167.500 de saldo líquido gerado no mês e R$ 22.500 aguardando conciliação. Como o BPO em D+1 e a alocação de caixa livre no CDI podem maximizar nosso retorno líquido sem risco de crédito?";

        StructuredDiagnosis diagnosis = manusService.performFinancialConsultation(saved, questaoExecutiva, bpoSummary);

        // Validação da Saída Estruturada
        assertNotNull(diagnosis);
        assertNotNull(diagnosis.getDiagnostico());
        assertFalse(diagnosis.getOportunidades().isEmpty());
        assertFalse(diagnosis.getAcoesPriorizadas().isEmpty());
        assertFalse(diagnosis.getMetricas().isEmpty());

        // 5. GOVERNANÇA FEDERADA DATA MESH FINANCEIRO
        DataMeshFederationRegistry registry = DataMeshFederationRegistry.getInstance();
        FinancialProfileProduct finProd = new FinancialProfileProduct(saved);
        BpoOperationsProduct bpoProd = new BpoOperationsProduct("batch-e2e-01", bpoSummary);

        assertTrue(registry.publishAndVerify(finProd), "Contrato de Perfil Financeiro deve ser aprovado pelo Data Mesh");
        assertTrue(registry.publishAndVerify(bpoProd), "Contrato de Operações BPO deve ser aprovado pelo Data Mesh");

        // 6. GERAÇÃO DA PASTA DE TRABALHO EXCEL (.xlsx)
        ExcelBpoIntegrationService excelService = new ExcelBpoIntegrationService();
        String excelPath = "./target/test-reports/E2E_Financial_Workbook.xlsx";
        File excelFile = excelService.exportFinancialWorkbook(saved, diagnosis, items, excelPath);

        assertTrue(excelFile.exists());
        assertTrue(excelFile.length() > 0);
        assertTrue(excelFile.getName().endsWith(".xlsx"));

        System.out.println(">>> SUCESSO TOTAL E2E MERCADO FINANCEIRO & BPO: Conformidade com Sigilo, RAG, Data Mesh e Excel comprovada!");
    }
}
