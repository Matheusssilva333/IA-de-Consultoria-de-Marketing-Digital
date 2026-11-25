package com.mycompany.digitalconsultant.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Saída estruturada da Consultoria com seções fixas (Conforme Fluxograma - Etapa 3).
 * - Diagnóstico
 * - Oportunidades
 * - Ações Priorizadas
 * - Métricas
 */
public class StructuredDiagnosis {

    private String id;
    private String diagnostico;
    private List<String> oportunidades;
    private List<ActionItem> acoesPriorizadas;
    private List<MetricTarget> metricas;
    
    // Telemetria de FinOps e Consumo de Tokens (Etapa 6)
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private double custoEstimadoUSD;
    private long latenciaMs;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public StructuredDiagnosis() {
        this.id = UUID.randomUUID().toString();
        this.oportunidades = new ArrayList<>();
        this.acoesPriorizadas = new ArrayList<>();
        this.metricas = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }

    public StructuredDiagnosis(String diagnostico, List<String> oportunidades,
                               List<ActionItem> acoesPriorizadas, List<MetricTarget> metricas) {
        this();
        this.diagnostico = diagnostico;
        this.oportunidades = oportunidades != null ? oportunidades : new ArrayList<>();
        this.acoesPriorizadas = acoesPriorizadas != null ? acoesPriorizadas : new ArrayList<>();
        this.metricas = metricas != null ? metricas : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public List<String> getOportunidades() {
        return oportunidades;
    }

    public void setOportunidades(List<String> oportunidades) {
        this.oportunidades = oportunidades;
    }

    public List<ActionItem> getAcoesPriorizadas() {
        return acoesPriorizadas;
    }

    public void setAcoesPriorizadas(List<ActionItem> acoesPriorizadas) {
        this.acoesPriorizadas = acoesPriorizadas;
    }

    public List<MetricTarget> getMetricas() {
        return metricas;
    }

    public void setMetricas(List<MetricTarget> metricas) {
        this.metricas = metricas;
    }

    public int getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(int promptTokens) {
        this.promptTokens = promptTokens;
    }

    public int getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(int completionTokens) {
        this.completionTokens = completionTokens;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

    public double getCustoEstimadoUSD() {
        return custoEstimadoUSD;
    }

    public void setCustoEstimadoUSD(double custoEstimadoUSD) {
        this.custoEstimadoUSD = custoEstimadoUSD;
    }

    public long getLatenciaMs() {
        return latenciaMs;
    }

    public void setLatenciaMs(long latenciaMs) {
        this.latenciaMs = latenciaMs;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
