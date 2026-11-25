package com.mycompany.digitalconsultant.bpo;

import com.mycompany.digitalconsultant.domain.model.BpoDashboardSummary;
import com.mycompany.digitalconsultant.domain.model.FinancialTransactionItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de Processamento Operacional de BPO Financeiro.
 * Executa conciliação bancária, fechamento de caixa e indicadores de saúde financeira.
 */
public class BpoProcessingService {

    public BpoDashboardSummary processTransactions(List<FinancialTransactionItem> items) {
        if (items == null || items.isEmpty()) {
            return new BpoDashboardSummary(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    0, 0, 100.0, List.of()
            );
        }

        BigDecimal totalReceitas = BigDecimal.ZERO;
        BigDecimal totalDespesas = BigDecimal.ZERO;
        BigDecimal totalAguardandoConciliacao = BigDecimal.ZERO;
        int conciliadasCount = 0;

        for (FinancialTransactionItem item : items) {
            BigDecimal valor = item.getValor() != null ? item.getValor().abs() : BigDecimal.ZERO;
            String tipo = item.getTipo() != null ? item.getTipo().toUpperCase() : "DESPESA";

            if (tipo.contains("RECEITA") || tipo.contains("ENTRADA") || tipo.contains("CAPTAÇÃO")) {
                totalReceitas = totalReceitas.add(valor);
            } else {
                totalDespesas = totalDespesas.add(valor);
            }

            if (item.isConciliado()) {
                conciliadasCount++;
            } else {
                totalAguardandoConciliacao = totalAguardandoConciliacao.add(valor);
            }
        }

        BigDecimal saldoLiquido = totalReceitas.subtract(totalDespesas);
        int total = items.size();
        double taxaConciliacao = total > 0 ? (conciliadasCount * 100.0) / total : 100.0;
        taxaConciliacao = BigDecimal.valueOf(taxaConciliacao).setScale(1, RoundingMode.HALF_UP).doubleValue();

        List<FinancialTransactionItem> destaques = new ArrayList<>(items);
        if (destaques.size() > 10) {
            destaques = destaques.subList(0, 10);
        }

        return new BpoDashboardSummary(
                totalReceitas, totalDespesas, saldoLiquido, totalAguardandoConciliacao,
                total, conciliadasCount, taxaConciliacao, destaques
        );
    }
}
