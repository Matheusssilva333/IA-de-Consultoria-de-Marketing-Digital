package com.mycompany.digitalconsultant.datamesh.domain;

import com.mycompany.digitalconsultant.datamesh.contract.DataContract;
import com.mycompany.digitalconsultant.datamesh.contract.DataProduct;

import java.util.List;
import java.util.Map;

/**
 * Produto de Dados do Domínio FinOps e Auditoria de Tokens/Custos.
 */
public class FinOpsTelemetryProduct implements DataProduct<FinOpsTelemetryProduct.TelemetryRecord> {

    public record TelemetryRecord(
            String operationId,
            String username,
            int promptTokens,
            int completionTokens,
            int totalTokens,
            double custoUSD,
            long latenciaMs,
            boolean sucesso,
            String statusInfo
    ) {}

    private final String productId;
    private final TelemetryRecord payload;
    private final DataContract contract;

    public FinOpsTelemetryProduct(TelemetryRecord payload) {
        this.payload = payload;
        this.productId = "prod-finops-" + payload.operationId();
        this.contract = new DataContract(
                "FinOpsAuditDomain",
                "FinOpsTelemetryDataProduct",
                "1.0.0",
                List.of("operationId", "username", "promptTokens", "completionTokens", "totalTokens", "custoUSD", "latenciaMs", "sucesso"),
                Map.of("custoUSD", "FinOps-Metric", "tokens", "Resource-Usage", "username", "Audit-Trail"),
                "SLA: Registro de telemetria em tempo real com controle de teto de custos"
        );
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public String getDomain() {
        return "FinOpsAuditDomain";
    }

    @Override
    public DataContract getDataContract() {
        return contract;
    }

    @Override
    public TelemetryRecord getPayload() {
        return payload;
    }

    @Override
    public boolean validateContract() {
        return payload.operationId() != null && payload.totalTokens() >= 0 && payload.custoUSD() >= 0;
    }
}
