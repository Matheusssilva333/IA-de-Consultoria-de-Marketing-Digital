package com.mycompany.digitalconsultant.domain.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resumo Gerencial de BPO Financeiro (DRE, Conciliação e Projeção).
 */
public record BpoDashboardSummary(
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldoLiquido,
        BigDecimal totalAguardandoConciliacao,
        int transacoesTotal,
        int transacoesConciliadas,
        double taxaConciliacaoPercent,
        List<FinancialTransactionItem> itensDestaque
) {}
