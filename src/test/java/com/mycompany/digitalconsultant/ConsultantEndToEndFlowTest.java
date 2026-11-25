package com.mycompany.digitalconsultant;

import com.mycompany.digitalconsultant.ai.ManusService;
import com.mycompany.digitalconsultant.database.ActionTrackingService;
import com.mycompany.digitalconsultant.database.CompanyProfileRepository;
import com.mycompany.digitalconsultant.database.ConsultationHistoryRepository;
import com.mycompany.digitalconsultant.database.DatabaseManager;
import com.mycompany.digitalconsultant.database.UserRepository;
import com.mycompany.digitalconsultant.domain.model.*;
import com.mycompany.digitalconsultant.report.PdfReportGenerator;
import com.mycompany.digitalconsultant.report.SpreadsheetCsvReportGenerator;
import com.mycompany.digitalconsultant.security.PasswordSecurity;
import com.mycompany.digitalconsultant.security.SessionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste End-to-End (E2E) - Jornada Completa do Fluxograma de Consultoria")
public class ConsultantEndToEndFlowTest {

    @BeforeAll
    static void setup() {
        DatabaseManager.getInstance().configureInMemoryMode("e2e_consultant_db");
    }

    @Test
    @DisplayName("Jornada E2E: Autenticação -> Coleta de Contexto -> LGPD -> RAG -> IA -> Histórico -> Ações -> Relatórios")
    void testCompleteConsultantEndToEndFlow() {
        // 1. AUTENTICAÇÃO E CIBERSEGURANÇA (OWASP)
        UserRepository userRepo = new UserRepository();
        SessionManager sessionManager = SessionManager.getInstance();

        String username = "sentinela_e2e";
        String password = "StrongPassword@2026";
        String salt = PasswordSecurity.generateSalt();
        String hash = PasswordSecurity.hashPassword(password, salt);

        userRepo.salvar(new UserAccount(username, hash, salt, "Matheus Silva", "matheus@sentinela.ai"));
        assertTrue(userRepo.buscarPorUsername(username).isPresent());

        // Login com verificação e emissão de token de sessão
        var user = userRepo.buscarPorUsername(username).get();
        assertTrue(PasswordSecurity.verifyPassword(password, user.getPasswordHash(), user.getSalt()));
        String sessionToken = sessionManager.createSession(username);
        assertTrue(sessionManager.isValidSession(sessionToken));

        // 2. COLETA DE CONTEXTO E PERFIL REUTILIZÁVEL (LGPD & CRIPTOGRAFIA AES-256)
        CompanyProfileRepository companyRepo = new CompanyProfileRepository();
        CompanyProfile profile = new CompanyProfile();
        profile.setId(UUID.randomUUID().toString());
        profile.setNomeEmpresa("PetroLogística & Crescimento Digital");
        profile.setSetorAtuacao("Logística e Suprimentos Industriais");
        profile.setPorte(PorteEmpresa.PEQUENA_MEDIA);
        profile.setOrcamentoMensal(new BigDecimal("12500.00"));
        profile.setCanais(Set.of(CanalMarketing.GOOGLE_ADS, CanalMarketing.LINKEDIN, CanalMarketing.WHATSAPP));
        profile.setMetas(Set.of(MetaMarketing.GERACAO_LEADS, MetaMarketing.REDUCAO_CAC));
        profile.setConsentimentoLGPD(true);
        profile.setDataConsentimento(LocalDateTime.now());
        profile.setUsuarioResponsavel(username);

        companyRepo.salvarOuAtualizar(profile);

        // Valida recuperação com decifragem transparente
        CompanyProfile savedProfile = companyRepo.buscarPorId(profile.getId()).orElseThrow();
        assertEquals(new BigDecimal("12500.00"), savedProfile.getOrcamentoMensal());
        assertTrue(savedProfile.isConsentimentoLGPD());

        // 3. CONSULTORIA ESTRATÉGICA COM RAG E MOTOR DE IA (FLUXOGRAMA ETAPAS 3, 4 E 6)
        ManusService service = new ManusService();
        String perguntaEstrategica = "Nosso ciclo de vendas está em 45 dias e o CAC em R$ 180,00. Como acelerar a qualificação pelo WhatsApp e reduzir o CAC usando Google Ads fundo de funil?";

        StructuredDiagnosis diagnosis = service.performConsultation(savedProfile, perguntaEstrategica);

        // Validação estrita das 4 seções fixas da saída estruturada
        assertNotNull(diagnosis);
        assertNotNull(diagnosis.getDiagnostico(), "Diagnóstico deve ser preenchido");
        assertFalse(diagnosis.getDiagnostico().isBlank());
        assertFalse(diagnosis.getOportunidades().isEmpty(), "Oportunidades devem ser preenchidas");
        assertFalse(diagnosis.getAcoesPriorizadas().isEmpty(), "Ações priorizadas devem ser preenchidas");
        assertFalse(diagnosis.getMetricas().isEmpty(), "Métricas devem ser preenchidas");
        assertTrue(diagnosis.getTotalTokens() > 0, "Telemetria de tokens deve registrar consumo");

        // 4. HISTÓRICO E ACOMPANHAMENTO DE AÇÕES (FLUXOGRAMA ETAPA 5)
        ConsultationHistoryRepository historyRepo = new ConsultationHistoryRepository();
        ActionTrackingService actionService = new ActionTrackingService();

        String consultaId = UUID.randomUUID().toString();
        ConsultationHistoryEntry historyEntry = new ConsultationHistoryEntry(
                consultaId, username, savedProfile.getId(), perguntaEstrategica, diagnosis, LocalDateTime.now()
        );
        historyRepo.salvarConsulta(historyEntry);

        List<ActionItem> acoesCadastradas = actionService.listarAcoesPorConsulta(consultaId);
        assertFalse(acoesCadastradas.isEmpty());

        // Simula execução prática do cliente marcando ações concluídas
        double progressoInicial = actionService.calcularProgressoImplementacao(consultaId);
        assertEquals(0.0, progressoInicial);

        actionService.marcarAcaoConcluida(acoesCadastradas.get(0).getId(), true);
        double progressoPosPrimeiraAcao = actionService.calcularProgressoImplementacao(consultaId);
        assertTrue(progressoPosPrimeiraAcao > 0.0);

        // 5. GERAÇÃO DE RELATÓRIO EXECUTIVO (PDF E PLANILHA CSV)
        PdfReportGenerator pdfGen = new PdfReportGenerator();
        SpreadsheetCsvReportGenerator csvGen = new SpreadsheetCsvReportGenerator();

        String pdfPath = "./target/test-reports/Relatorio_E2E.pdf";
        String csvPath = "./target/test-reports/Planilha_E2E.csv";

        File pdfFile = pdfGen.generatePdfReport(savedProfile, diagnosis, pdfPath);
        File csvFile = csvGen.generateCsvReport(savedProfile, diagnosis, csvPath);

        assertTrue(pdfFile.exists() && pdfFile.length() > 0, "O arquivo PDF deve ser gerado fisicamente com conteúdo");
        assertTrue(csvFile.exists() && csvFile.length() > 0, "O arquivo CSV deve ser gerado fisicamente com conteúdo");

        System.out.println(">>> SUCESSO TOTAL E2E: Ciclo completo do fluxograma executado com conformidade Rank A!");
    }
}
