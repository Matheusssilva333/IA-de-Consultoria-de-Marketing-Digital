package com.mycompany.digitalconsultant.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.digitalconsultant.domain.model.FinancialProfile;
import com.mycompany.digitalconsultant.domain.model.ModuloBpo;
import com.mycompany.digitalconsultant.domain.model.ObjetivoMercadoFinanceiro;
import com.mycompany.digitalconsultant.domain.model.TipoInstituicaoFinanceira;
import com.mycompany.digitalconsultant.security.CryptoUtils;
import com.mycompany.digitalconsultant.security.LgpdComplianceService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Repositório de Perfis Financeiros e BPO com Criptografia em Repouso AES-256 e Sigilo Bancário.
 */
public class FinancialProfileRepository {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();
    private final LgpdComplianceService lgpdService = LgpdComplianceService.getInstance();

    public void salvarOuAtualizar(FinancialProfile profile) {
        if (profile.getId() == null || profile.getId().isBlank()) {
            profile.setId(UUID.randomUUID().toString());
        }

        // Criptografa o AUM/Faturamento com AES-256-GCM para sigilo bancário irrestrito
        String aumCifrado = CryptoUtils.encrypt(
                profile.getAumOuFaturamentoMensal() != null ? profile.getAumOuFaturamentoMensal().toString() : "0.00"
        );

        String sql = """
            MERGE INTO tb_perfis_financeiros (id, nome_instituicao, tipo, aum_cifrado, regime_tributario, modulos_bpo_json, objetivos_json, consentimento_sigilo, data_consentimento, responsavel_financeiro)
            KEY (id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getId());
            ps.setString(2, profile.getNomeInstituicao());
            ps.setString(3, profile.getTipo() != null ? profile.getTipo().name() : TipoInstituicaoFinanceira.EMPRESA_CLIENTE_BPO.name());
            ps.setString(4, aumCifrado);
            ps.setString(5, profile.getRegimeTributario());
            ps.setString(6, mapper.writeValueAsString(profile.getModulosBpoAtivos()));
            ps.setString(7, mapper.writeValueAsString(profile.getObjetivos()));
            ps.setBoolean(8, profile.isConsentimentoLgpdSigilo());
            ps.setString(9, profile.getDataConsentimento() != null ? profile.getDataConsentimento().toString() : LocalDateTime.now().toString());
            ps.setString(10, profile.getResponsavelFinanceiro());

            ps.executeUpdate();

            lgpdService.logAudit("SALVAR_PERFIL_FINANCEIRO_BPO", profile.getResponsavelFinanceiro(),
                    "Perfil financeiro persistido com criptografia AES-256 para: " + profile.getNomeInstituicao(), true);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar perfil financeiro: " + e.getMessage(), e);
        }
    }

    public Optional<FinancialProfile> buscarPorUsuario(String responsavel) {
        if (responsavel == null) return Optional.empty();
        String sql = "SELECT * FROM tb_perfis_financeiros WHERE LOWER(responsavel_financeiro) = LOWER(?) ORDER BY data_consentimento DESC LIMIT 1;";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, responsavel.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar perfil financeiro: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<FinancialProfile> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM tb_perfis_financeiros WHERE id = ?;";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar perfil financeiro por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private FinancialProfile mapResultSet(ResultSet rs) throws Exception {
        FinancialProfile p = new FinancialProfile();
        p.setId(rs.getString("id"));
        p.setNomeInstituicao(rs.getString("nome_instituicao"));
        p.setTipo(TipoInstituicaoFinanceira.valueOf(rs.getString("tipo")));

        String aumCifrado = rs.getString("aum_cifrado");
        String aumDecifrado = CryptoUtils.decrypt(aumCifrado);
        p.setAumOuFaturamentoMensal(new BigDecimal(aumDecifrado));

        p.setRegimeTributario(rs.getString("regime_tributario"));

        String modJson = rs.getString("modulos_bpo_json");
        if (modJson != null && !modJson.isBlank()) {
            p.setModulosBpoAtivos(mapper.readValue(modJson, new TypeReference<Set<ModuloBpo>>() {}));
        }

        String objJson = rs.getString("objetivos_json");
        if (objJson != null && !objJson.isBlank()) {
            p.setObjetivos(mapper.readValue(objJson, new TypeReference<Set<ObjetivoMercadoFinanceiro>>() {}));
        }

        p.setConsentimentoLgpdSigilo(rs.getBoolean("consentimento_sigilo"));
        String dt = rs.getString("data_consentimento");
        if (dt != null) p.setDataConsentimento(LocalDateTime.parse(dt));
        p.setResponsavelFinanceiro(rs.getString("responsavel_financeiro"));
        return p;
    }
}
