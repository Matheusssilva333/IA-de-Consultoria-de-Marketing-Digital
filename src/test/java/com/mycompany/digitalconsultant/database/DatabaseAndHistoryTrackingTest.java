package com.mycompany.digitalconsultant.database;

import com.mycompany.digitalconsultant.domain.model.*;
import com.mycompany.digitalconsultant.security.PasswordSecurity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Persistência no Banco H2 e Acompanhamento de Ações")
public class DatabaseAndHistoryTrackingTest {

    @BeforeAll
    static void setup() {
        DatabaseManager.getInstance().configureInMemoryMode("test_db_" + System.currentTimeMillis());
    }

    @Test
    @DisplayName("Deve salvar e autenticar usuário com senha criptografada")
    void testUserPersistence() {
        UserRepository repo = new UserRepository();
        String username = "user_test_" + System.currentTimeMillis();
        String salt = PasswordSecurity.generateSalt();
        String hash = PasswordSecurity.hashPassword("SenhaForte@123", salt);

        UserAccount account = new UserAccount(username, hash, salt, "Tester QA", "tester@consultoria.com");
        repo.salvar(account);

        Optional<UserAccount> found = repo.buscarPorUsername(username);
        assertTrue(found.isPresent());
        assertEquals("Tester QA", found.get().getNomeCompleto());
        assertTrue(PasswordSecurity.verifyPassword("SenhaForte@123", found.get().getPasswordHash(), found.get().getSalt()));
    }

    @Test
    @DisplayName("Deve salvar perfil da empresa criptografando orçamento e recuperando com precisão")
    void testCompanyProfilePersistenceWithEncryption() {
        CompanyProfileRepository repo = new CompanyProfileRepository();
        String id = UUID.randomUUID().toString();
        CompanyProfile profile = new CompanyProfile(
                id, "Empresa Alpha Ltda", "Varejo", PorteEmpresa.PEQUENA_MEDIA,
                Set.of(CanalMarketing.INSTAGRAM, CanalMarketing.WHATSAPP),
                new BigDecimal("15000.50"),
                Set.of(MetaMarketing.GERACAO_LEADS),
                true, LocalDateTime.now(), "user_owner"
        );

        repo.salvarOuAtualizar(profile);

        Optional<CompanyProfile> loaded = repo.buscarPorId(id);
        assertTrue(loaded.isPresent());
        assertEquals("Empresa Alpha Ltda", loaded.get().getNomeEmpresa());
        assertEquals(new BigDecimal("15000.50"), loaded.get().getOrcamentoMensal());
        assertTrue(loaded.get().isConsentimentoLGPD());
    }

    @Test
    @DisplayName("Deve persistir histórico de consulta e gerenciar checklist de ações com percentual")
    void testHistoryAndActionTracking() {
        ConsultationHistoryRepository historyRepo = new ConsultationHistoryRepository();
        ActionTrackingService actionService = new ActionTrackingService();

        String consultaId = UUID.randomUUID().toString();
        StructuredDiagnosis diag = new StructuredDiagnosis();
        diag.setDiagnostico("Diagnóstico Histórico");
        diag.getOportunidades().add("Oportunidade A");

        ActionItem a1 = new ActionItem("A1", "Ação 1", "Detalhe 1", "ALTO", "BAIXO", "3 dias", false);
        ActionItem a2 = new ActionItem("A2", "Ação 2", "Detalhe 2", "MEDIO", "MEDIO", "7 dias", false);
        diag.setAcoesPriorizadas(List.of(a1, a2));

        ConsultationHistoryEntry entry = new ConsultationHistoryEntry(
                consultaId, "user_tracking", "emp_1", "Pergunta teste?", diag, LocalDateTime.now()
        );
        historyRepo.salvarConsulta(entry);

        // Verifica ações salvas
        List<ActionItem> acoes = actionService.listarAcoesPorConsulta(consultaId);
        assertEquals(2, acoes.size());
        assertEquals(0.0, actionService.calcularProgressoImplementacao(consultaId));

        // Marca a primeira ação como concluída
        actionService.marcarAcaoConcluida(a1.getId(), true);
        assertEquals(50.0, actionService.calcularProgressoImplementacao(consultaId), 0.1);

        // Marca a segunda ação como concluída
        actionService.marcarAcaoConcluida(a2.getId(), true);
        assertEquals(100.0, actionService.calcularProgressoImplementacao(consultaId), 0.1);
    }
}
