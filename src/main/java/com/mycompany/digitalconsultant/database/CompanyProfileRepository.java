package com.mycompany.digitalconsultant.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.digitalconsultant.domain.model.CanalMarketing;
import com.mycompany.digitalconsultant.domain.model.CompanyProfile;
import com.mycompany.digitalconsultant.domain.model.MetaMarketing;
import com.mycompany.digitalconsultant.domain.model.PorteEmpresa;
import com.mycompany.digitalconsultant.security.CryptoUtils;
import com.mycompany.digitalconsultant.security.LgpdComplianceService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Repositório seguro de Perfil da Empresa (Reutilizável & Criptografado - Etapa 2).
 */
public class CompanyProfileRepository {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();
    private final LgpdComplianceService lgpdService = LgpdComplianceService.getInstance();

    public void salvarOuAtualizar(CompanyProfile profile) {
        Objects.requireNonNull(profile, "Perfil não pode ser nulo");
        if (profile.getId() == null || profile.getId().isBlank()) {
            profile.setId(UUID.randomUUID().toString());
        }

        // Criptografia AES-256 de dados confidenciais financeiros (LGPD)
        String orcamentoCifrado = CryptoUtils.encrypt(
                profile.getOrcamentoMensal() != null ? profile.getOrcamentoMensal().toString() : "0.00"
        );

        String sql = """
            MERGE INTO tb_empresas (id, nome_empresa, setor, porte, canais_json, orcamento_cifrado, metas_json, consentimento_lgpd, data_consentimento, usuario_responsavel)
            KEY (id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getId());
            ps.setString(2, profile.getNomeEmpresa());
            ps.setString(3, profile.getSetorAtuacao());
            ps.setString(4, profile.getPorte() != null ? profile.getPorte().name() : PorteEmpresa.PEQUENA_MEDIA.name());
            ps.setString(5, mapper.writeValueAsString(profile.getCanais()));
            ps.setString(6, orcamentoCifrado);
            ps.setString(7, mapper.writeValueAsString(profile.getMetas()));
            ps.setBoolean(8, profile.isConsentimentoLGPD());
            ps.setString(9, profile.getDataConsentimento() != null ? profile.getDataConsentimento().toString() : LocalDateTime.now().toString());
            ps.setString(10, profile.getUsuarioResponsavel());

            ps.executeUpdate();

            lgpdService.logAudit("SALVAR_PERFIL_EMPRESA", profile.getUsuarioResponsavel(),
                    "Perfil persistido com criptografia AES-256 para: " + profile.getNomeEmpresa(), true);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar perfil da empresa: " + e.getMessage(), e);
        }
    }

    public Optional<CompanyProfile> buscarPorUsuario(String username) {
        if (username == null) return Optional.empty();
        String sql = "SELECT * FROM tb_empresas WHERE LOWER(usuario_responsavel) = LOWER(?) ORDER BY data_consentimento DESC LIMIT 1;";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProfile(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar perfil da empresa: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<CompanyProfile> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM tb_empresas WHERE id = ?;";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProfile(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar perfil por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void removerPorId(String id, String usuario) {
        String sql = "DELETE FROM tb_empresas WHERE id = ?;";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            lgpdService.logAudit("REMOVER_PERFIL_LGPD", usuario, "Perfil removido ID: " + id, true);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir perfil: " + e.getMessage(), e);
        }
    }

    private CompanyProfile mapResultSetToProfile(ResultSet rs) throws Exception {
        CompanyProfile p = new CompanyProfile();
        p.setId(rs.getString("id"));
        p.setNomeEmpresa(rs.getString("nome_empresa"));
        p.setSetorAtuacao(rs.getString("setor"));
        p.setPorte(PorteEmpresa.valueOf(rs.getString("porte")));

        // Decifragem AES-256 do orçamento
        String orcamentoCifrado = rs.getString("orcamento_cifrado");
        String orcamentoDecifrado = CryptoUtils.decrypt(orcamentoCifrado);
        p.setOrcamentoMensal(new BigDecimal(orcamentoDecifrado));

        String canaisJson = rs.getString("canais_json");
        if (canaisJson != null && !canaisJson.isBlank()) {
            Set<CanalMarketing> canais = mapper.readValue(canaisJson, new TypeReference<Set<CanalMarketing>>() {});
            p.setCanais(canais);
        }

        String metasJson = rs.getString("metas_json");
        if (metasJson != null && !metasJson.isBlank()) {
            Set<MetaMarketing> metas = mapper.readValue(metasJson, new TypeReference<Set<MetaMarketing>>() {});
            p.setMetas(metas);
        }

        p.setConsentimentoLGPD(rs.getBoolean("consentimento_lgpd"));
        String dataCons = rs.getString("data_consentimento");
        if (dataCons != null) {
            p.setDataConsentimento(LocalDateTime.parse(dataCons));
        }
        p.setUsuarioResponsavel(rs.getString("usuario_responsavel"));
        return p;
    }
}
