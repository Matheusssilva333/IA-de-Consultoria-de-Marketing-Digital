package com.mycompany.digitalconsultant.domain.model;

public enum MetaMarketing {
    GERACAO_LEADS("Geração de Leads Qualificados (B2B/Serviços)"),
    VENDAS_ECOMMERCE("Aumento de Vendas Diretas no E-commerce"),
    AWARENESS_MARCA("Reconhecimento e Fortalecimento de Marca"),
    REDUCAO_CAC("Otimização de Conversão e Redução de CAC"),
    AUMENTO_LTV("Maximização de Ticket Médio e LTV"),
    RETENCAO_CHURN("Retenção de Clientes e Redução de Churn");

    private final String descricao;

    MetaMarketing(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
