package com.mycompany.digitalconsultant.domain.model;

public enum ObjetivoMercadoFinanceiro {
    AUMENTO_AUM("Aumento de Patrimônio sob Gestão (Captação de AUM)"),
    EFICIENCIA_OPERACIONAL_BPO("Eficiência de BPO: Fechamento em D+1 e Zero Erro Contábil"),
    MAXIMIZACAO_LIQUIDEZ("Rentabilização do Caixa Livre e Tesouraria (CDI+)"),
    MITIGACAO_RISCO_CREDITO("Mitigação de Inadimplência e Análise de Risco de Crédito"),
    COMPLIANCE_BACEN_CVM("Conformidade e Compliance Regulatório (Bacen / CVM / Anbima)");

    private final String descricao;

    ObjetivoMercadoFinanceiro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
