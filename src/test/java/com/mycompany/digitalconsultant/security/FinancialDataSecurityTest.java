package com.mycompany.digitalconsultant.security;

import com.mycompany.digitalconsultant.database.DatabaseManager;
import com.mycompany.digitalconsultant.database.FinancialProfileRepository;
import com.mycompany.digitalconsultant.domain.model.FinancialProfile;
import com.mycompany.digitalconsultant.domain.model.TipoInstituicaoFinanceira;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Blindagem de Dados Financeiros, Sigilo Bancário e LGPD")
public class FinancialDataSecurityTest {

    @BeforeAll
    static void setup() {
        DatabaseManager.getInstance().configureInMemoryMode("fin_security_test_db");
    }

    @Test
    @DisplayName("Garante que o valor patrimonial (AUM) NUNCA seja gravado em texto plano no banco de dados")
    void testAumNeverStoredInPlainText() throws Exception {
        FinancialProfileRepository repo = new FinancialProfileRepository();
        String id = UUID.randomUUID().toString();
        BigDecimal valorConfidencial = new BigDecimal("87654321.99");

        FinancialProfile profile = new FinancialProfile();
        profile.setId(id);
        profile.setNomeInstituicao("Family Office Blindado");
        profile.setTipo(TipoInstituicaoFinanceira.FAMILY_OFFICE);
        profile.setAumOuFaturamentoMensal(valorConfidencial);
        profile.setConsentimentoLgpdSigilo(true);
        profile.setResponsavelFinanceiro("gestor_compliance");

        repo.salvarOuAtualizar(profile);

        // Consulta direta JDBC no banco para provar ausência de texto puro
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT aum_cifrado FROM tb_perfis_financeiros WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                String valorNoDisco = rs.getString("aum_cifrado");

                // Prova 1: O valor no disco NÃO contém os dígitos do patrimônio em texto puro
                assertFalse(valorNoDisco.contains("87654321"), "VULNERABILIDADE: O valor confidencial apareceu em texto puro no banco!");
                // Prova 2: O valor está em formato Base64 criptografado
                assertDoesNotThrow(() -> java.util.Base64.getDecoder().decode(valorNoDisco));
            }
        }

        // Prova 3: A recuperação através do repositório decifra com perfeição matemática
        FinancialProfile carregado = repo.buscarPorId(id).orElseThrow();
        assertEquals(valorConfidencial, carregado.getAumOuFaturamentoMensal());
    }

    @Test
    @DisplayName("Garante mascaramento seguro de dados bancários e dados de clientes contra vazamento")
    void testBankingDataMasking() {
        LgpdComplianceService lgpd = LgpdComplianceService.getInstance();

        String contaBancaria = "0001-98765432-1";
        String mascarada = lgpd.maskSensitiveData(contaBancaria);

        assertNotEquals(contaBancaria, mascarada);
        assertTrue(mascarada.contains("****"));
        assertTrue(mascarada.startsWith("00"));
        assertTrue(mascarada.endsWith("-1"));
    }
}
