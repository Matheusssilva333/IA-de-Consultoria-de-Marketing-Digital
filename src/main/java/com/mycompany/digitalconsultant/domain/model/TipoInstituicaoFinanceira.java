package com.mycompany.digitalconsultant.domain.model;

public enum TipoInstituicaoFinanceira {
    GESTORA_ASSET("Asset Management / Gestora de Recursos"),
    ESCRITORIO_ASSESSORIA("Assessoria de Investimentos (AIs / M&A)"),
    FAMILY_OFFICE("Wealth Management & Multi-Family Office"),
    FINTECH_CREDITO("Fintech / SCD / Sociedade de Crédito Direto"),
    EMPRESA_CLIENTE_BPO("PME / Corporação com BPO Financeiro");

    private final String descricao;

    TipoInstituicaoFinanceira(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
