package com.mycompany.digitalconsultant.datamesh.domain;

import com.mycompany.digitalconsultant.datamesh.contract.DataContract;
import com.mycompany.digitalconsultant.datamesh.contract.DataProduct;
import com.mycompany.digitalconsultant.domain.model.StructuredDiagnosis;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Produto de Dados do Domínio de Inteligência de Marketing.
 */
public class MarketingIntelligenceProduct implements DataProduct<StructuredDiagnosis> {

    private final String productId;
    private final StructuredDiagnosis payload;
    private final DataContract contract;

    public MarketingIntelligenceProduct(StructuredDiagnosis payload) {
        this.payload = Objects.requireNonNull(payload, "Payload não pode ser nulo");
        this.productId = "prod-mkt-intel-" + (payload.getId() != null ? payload.getId() : "temp");
        this.contract = new DataContract(
                "MarketingIntelligenceDomain",
                "StructuredDiagnosisDataProduct",
                "1.0.0",
                List.of("id", "diagnostico", "oportunidades", "acoesPriorizadas", "metricas"),
                Map.of("diagnostico", "Estratégico", "acoesPriorizadas", "Operacional", "metricas", "KPIs"),
                "SLA: Resposta estruturada JSON com seções fixas e rastreabilidade de tokens"
        );
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public String getDomain() {
        return "MarketingIntelligenceDomain";
    }

    @Override
    public DataContract getDataContract() {
        return contract;
    }

    @Override
    public StructuredDiagnosis getPayload() {
        return payload;
    }

    @Override
    public boolean validateContract() {
        if (payload.getDiagnostico() == null || payload.getDiagnostico().isBlank()) {
            return false;
        }
        if (payload.getOportunidades() == null || payload.getOportunidades().isEmpty()) {
            return false;
        }
        if (payload.getAcoesPriorizadas() == null || payload.getAcoesPriorizadas().isEmpty()) {
            return false;
        }
        return payload.getMetricas() != null && !payload.getMetricas().isEmpty();
    }
}
