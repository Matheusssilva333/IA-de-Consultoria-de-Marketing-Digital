package com.mycompany.digitalconsultant.domain.model;

import java.util.UUID;

/**
 * Item de Ação Priorizada da Consultoria com checklist de execução.
 */
public class ActionItem {

    private String id;
    private String titulo;
    private String descricao;
    private String impacto; // ALTO, MEDIO, BAIXO
    private String esforco; // ALTO, MEDIO, BAIXO
    private String prazoSugerido;
    private boolean concluida;

    public ActionItem() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
    }

    public ActionItem(String titulo, String descricao, String impacto, String esforco, String prazoSugerido) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.titulo = titulo;
        this.descricao = descricao;
        this.impacto = impacto != null ? impacto.toUpperCase() : "MEDIO";
        this.esforco = esforco != null ? esforco.toUpperCase() : "MEDIO";
        this.prazoSugerido = prazoSugerido != null ? prazoSugerido : "14 dias";
        this.concluida = false;
    }

    public ActionItem(String id, String titulo, String descricao, String impacto, String esforco, String prazoSugerido, boolean concluida) {
        this.id = id != null ? id : UUID.randomUUID().toString().substring(0, 8);
        this.titulo = titulo;
        this.descricao = descricao;
        this.impacto = impacto;
        this.esforco = esforco;
        this.prazoSugerido = prazoSugerido;
        this.concluida = concluida;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImpacto() {
        return impacto;
    }

    public void setImpacto(String impacto) {
        this.impacto = impacto;
    }

    public String getEsforco() {
        return esforco;
    }

    public void setEsforco(String esforco) {
        this.esforco = esforco;
    }

    public String getPrazoSugerido() {
        return prazoSugerido;
    }

    public void setPrazoSugerido(String prazoSugerido) {
        this.prazoSugerido = prazoSugerido;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }
}
