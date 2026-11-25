package com.mycompany.digitalconsultant.datamesh.domain;

import com.mycompany.digitalconsultant.datamesh.contract.DataContract;
import com.mycompany.digitalconsultant.datamesh.contract.DataProduct;
import com.mycompany.digitalconsultant.domain.model.FinancialProfile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Produto de Dados do Domínio de Identidade de Instituição Financeira e BPO.
 */
public class FinancialProfileProduct implements DataProduct<FinancialProfile> {

    private final String productId;
    private final FinancialProfile payload;
    private final DataContract contract;

    public FinancialProfileProduct(FinancialProfile payload) {
        this.payload = Objects.requireNonNull(payload, "Payload não pode ser nulo");
        this.productId = "prod-fin-profile-" + (payload.getId() != null ? payload.getId() : "temp");
        this.contract = new DataContract(
                "FinancialProfileDomain",
                "FinancialProfileDataProduct",
                "2.0.0",
                List.of("id", "nomeInstituicao", "tipo", "aumOuFaturamentoMensal", "regimeTributario", "modulosBpoAtivos", "objetivos", "consentimentoLgpdSigilo"),
                Map.of(
                        "nomeInstituicao", "Confidencial",
                        "aumOuFaturamentoMensal", "Sigilo-Bancário-LC-105/01",
                        "consentimentoLgpdSigilo", "Mandatório-Regulatório-LGPD"
                ),
                "SLA: 99.99% consistência contábil, criptografia AES-256 em repouso e sigilo bancário"
        );
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public String getDomain() {
        return "FinancialProfileDomain";
    }

    @Override
    public DataContract getDataContract() {
        return contract;
    }

    @Override
    public FinancialProfile getPayload() {
        return payload;
    }

    @Override
    public boolean validateContract() {
        if (payload.getNomeInstituicao() == null || payload.getNomeInstituicao().isBlank()) {
            return false;
        }
        if (payload.getAumOuFaturamentoMensal() == null || payload.getAumOuFaturamentoMensal().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        // Sob as normas de sigilo e LGPD, o consentimento formal é mandatório
        return payload.isConsentimentoLgpdSigilo();
    }
}
