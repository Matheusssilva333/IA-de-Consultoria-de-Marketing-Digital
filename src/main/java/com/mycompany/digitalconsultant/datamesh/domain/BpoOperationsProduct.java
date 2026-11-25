package com.mycompany.digitalconsultant.datamesh.domain;

import com.mycompany.digitalconsultant.datamesh.contract.DataContract;
import com.mycompany.digitalconsultant.datamesh.contract.DataProduct;
import com.mycompany.digitalconsultant.domain.model.BpoDashboardSummary;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Produto de Dados do Domínio de Operações de BPO Financeiro e Conciliação.
 */
public class BpoOperationsProduct implements DataProduct<BpoDashboardSummary> {

    private final String productId;
    private final BpoDashboardSummary payload;
    private final DataContract contract;

    public BpoOperationsProduct(String operationBatchId, BpoDashboardSummary payload) {
        this.payload = Objects.requireNonNull(payload, "Payload de BPO não pode ser nulo");
        this.productId = "prod-bpo-ops-" + operationBatchId;
        this.contract = new DataContract(
                "BpoOperationsDomain",
                "BpoOperationalDataProduct",
                "2.0.0",
                List.of("totalReceitas", "totalDespesas", "saldoLiquido", "totalAguardandoConciliacao", "taxaConciliacaoPercent"),
                Map.of(
                        "saldoLiquido", "Métrica-Patrimonial",
                        "taxaConciliacaoPercent", "KPI-Operacional-BPO"
                ),
                "SLA: Fechamento em D+1, tolerância zero para furos de conciliação bancária"
        );
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public String getDomain() {
        return "BpoOperationsDomain";
    }

    @Override
    public DataContract getDataContract() {
        return contract;
    }

    @Override
    public BpoDashboardSummary getPayload() {
        return payload;
    }

    @Override
    public boolean validateContract() {
        if (payload.totalReceitas() == null || payload.totalDespesas() == null || payload.saldoLiquido() == null) {
            return false;
        }
        // Integridade Contábil: Saldo Líquido deve ser exatamente Receitas - Despesas
        BigDecimal esperado = payload.totalReceitas().subtract(payload.totalDespesas());
        if (payload.saldoLiquido().compareTo(esperado) != 0) {
            return false;
        }
        return payload.taxaConciliacaoPercent() >= 0.0 && payload.taxaConciliacaoPercent() <= 100.0;
    }
}
