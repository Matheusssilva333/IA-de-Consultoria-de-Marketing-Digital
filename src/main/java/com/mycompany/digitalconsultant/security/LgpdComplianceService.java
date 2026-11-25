package com.mycompany.digitalconsultant.security;

import com.mycompany.digitalconsultant.domain.model.CompanyProfile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Serviço de Governança e Compliance com a LGPD (Lei Geral de Proteção de Dados - Lei 13.709/2018).
 * - Termo e registro de Consentimento (Art. 7º e 8º)
 * - Anonimização e Pseudonimização (Art. 13)
 * - Direito ao Esquecimento / Expurgo de dados (Art. 18, VI)
 * - Trilha de Auditoria Imutável (Audit Trail)
 */
public class LgpdComplianceService {

    private static final Logger LOGGER = Logger.getLogger(LgpdComplianceService.class.getName());
    private static final LgpdComplianceService INSTANCE = new LgpdComplianceService();

    public record AuditLogEntry(
            LocalDateTime timestamp,
            String operacao,
            String usuario,
            String detalhe,
            boolean statusSucesso
    ) {}

    private final List<AuditLogEntry> auditTrail = Collections.synchronizedList(new ArrayList<>());

    private LgpdComplianceService() {}

    public static LgpdComplianceService getInstance() {
        return INSTANCE;
    }

    public void logAudit(String operacao, String usuario, String detalhe, boolean sucesso) {
        AuditLogEntry entry = new AuditLogEntry(LocalDateTime.now(), operacao, usuario, detalhe, sucesso);
        auditTrail.add(entry);
        LOGGER.info(() -> String.format("[LGPD AUDIT] Op: %s | User: %s | Sucesso: %s | Detalhe: %s",
                operacao, usuario, sucesso, detalhe));
    }

    public boolean validateConsent(CompanyProfile profile) {
        if (profile == null) return false;
        boolean valido = profile.isConsentimentoLGPD() && profile.getDataConsentimento() != null;
        logAudit("VALIDAR_CONSENTIMENTO_LGPD", profile.getUsuarioResponsavel(),
                "Empresa: " + profile.getNomeEmpresa() + " | Consentimento: " + valido, valido);
        return valido;
    }

    /**
     * Aplica mascaramento de dados (Pseudonimização) para logs e exibições seguras.
     */
    public String maskSensitiveData(String input) {
        if (input == null || input.length() <= 4) {
            return "****";
        }
        return input.substring(0, 2) + "****" + input.substring(input.length() - 2);
    }

    /**
     * Anonimiza perfil de empresa em atendimento ao direito de esquecimento (Art. 18 LGPD).
     */
    public CompanyProfile anonymizeProfile(CompanyProfile profile) {
        if (profile == null) return null;
        logAudit("EXERCICIO_DIREITO_ESQUECIMENTO", profile.getUsuarioResponsavel(),
                "Anonimizando empresa ID: " + profile.getId(), true);

        profile.setNomeEmpresa("EMPRESA_ANONIMIZADA_" + System.currentTimeMillis());
        profile.setSetorAtuacao("SETOR_RETIDO");
        profile.setUsuarioResponsavel("USUARIO_EXPURGADO");
        return profile;
    }

    public List<AuditLogEntry> getAuditTrail() {
        return Collections.unmodifiableList(auditTrail);
    }
}
