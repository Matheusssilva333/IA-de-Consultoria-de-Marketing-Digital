package com.mycompany.digitalconsultant.domain.model;

public enum ModuloBpo {
    CONTAS_PAGAR_RECEBER("Contas a Pagar e Receber"),
    CONCILIACAO_BANCARIA("Conciliação Bancária & Fechamento Diário"),
    FLUXO_CAIXA_DRE("Fluxo de Caixa Projetado & DRE Gerencial"),
    GESTAO_INADIMPLENCIA("Gestão de Inadimplência & Régua de Cobrança"),
    PLANEJAMENTO_TRIBUTARIO("Planejamento Tributário & Compliance Fiscal");

    private final String descricao;

    ModuloBpo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
