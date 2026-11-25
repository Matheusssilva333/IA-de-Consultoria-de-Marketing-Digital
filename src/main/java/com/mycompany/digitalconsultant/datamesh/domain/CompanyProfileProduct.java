package com.mycompany.digitalconsultant.datamesh.domain;

import com.mycompany.digitalconsultant.datamesh.contract.DataContract;
import com.mycompany.digitalconsultant.datamesh.contract.DataProduct;
import com.mycompany.digitalconsultant.domain.model.CompanyProfile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Produto de Dados do Domínio de Identidade e Perfil de Empresa (LGPD Compliant).
 */
public class CompanyProfileProduct implements DataProduct<CompanyProfile> {

    private final String productId;
    private final CompanyProfile payload;
    private final DataContract contract;

    public CompanyProfileProduct(CompanyProfile payload) {
        this.payload = Objects.requireNonNull(payload, "Payload não pode ser nulo");
        this.productId = "prod-company-" + (payload.getId() != null ? payload.getId() : "temp");
        this.contract = new DataContract(
                "CompanyProfileDomain",
                "CompanyProfileDataProduct",
                "1.0.0",
                List.of("id", "nomeEmpresa", "setorAtuacao", "porte", "canais", "orcamentoMensal", "metas", "consentimentoLGPD"),
                Map.of("nomeEmpresa", "Confidencial", "orcamentoMensal", "Sensível-Negócio", "consentimentoLGPD", "Mandatório-LGPD"),
                "SLA: 99.9% consistência, criptografia AES-256 em repouso obrigatória"
        );
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public String getDomain() {
        return "CompanyProfileDomain";
    }

    @Override
    public DataContract getDataContract() {
        return contract;
    }

    @Override
    public CompanyProfile getPayload() {
        return payload;
    }

    @Override
    public boolean validateContract() {
        if (payload.getNomeEmpresa() == null || payload.getNomeEmpresa().isBlank()) {
            return false;
        }
        if (payload.getOrcamentoMensal() == null) {
            return false;
        }
        // Sob a LGPD, o consentimento é estritamente obrigatório
        return payload.isConsentimentoLGPD();
    }
}
