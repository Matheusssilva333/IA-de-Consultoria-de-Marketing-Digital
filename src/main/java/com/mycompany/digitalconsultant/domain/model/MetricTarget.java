package com.mycompany.digitalconsultant.domain.model;

/**
 * Meta de métrica (KPI) recomendada no diagnóstico.
 */
public class MetricTarget {

    private String metrica;
    private String valorAlvo;
    private String explicacao;

    public MetricTarget() {
    }

    public MetricTarget(String metrica, String valorAlvo, String explicacao) {
        this.metrica = metrica;
        this.valorAlvo = valorAlvo;
        this.explicacao = explicacao;
    }

    public String getMetrica() {
        return metrica;
    }

    public void setMetrica(String metrica) {
        this.metrica = metrica;
    }

    public String getValorAlvo() {
        return valorAlvo;
    }

    public void setValorAlvo(String valorAlvo) {
        this.valorAlvo = valorAlvo;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }
}
