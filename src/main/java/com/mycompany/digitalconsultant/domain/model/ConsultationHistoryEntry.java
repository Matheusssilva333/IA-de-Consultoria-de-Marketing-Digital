package com.mycompany.digitalconsultant.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Registro de histórico e acompanhamento evolutivo (Etapa 5 do fluxograma).
 */
public class ConsultationHistoryEntry {

    private String id;
    private String usuario;
    private String empresaId;
    private String perguntaOriginal;
    private StructuredDiagnosis resultado;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHora;

    public ConsultationHistoryEntry() {
        this.dataHora = LocalDateTime.now();
    }

    public ConsultationHistoryEntry(String id, String usuario, String empresaId,
                                    String perguntaOriginal, StructuredDiagnosis resultado,
                                    LocalDateTime dataHora) {
        this.id = id;
        this.usuario = usuario;
        this.empresaId = empresaId;
        this.perguntaOriginal = perguntaOriginal;
        this.resultado = resultado;
        this.dataHora = dataHora != null ? dataHora : LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getPerguntaOriginal() {
        return perguntaOriginal;
    }

    public void setPerguntaOriginal(String perguntaOriginal) {
        this.perguntaOriginal = perguntaOriginal;
    }

    public StructuredDiagnosis getResultado() {
        return resultado;
    }

    public void setResultado(StructuredDiagnosis resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
