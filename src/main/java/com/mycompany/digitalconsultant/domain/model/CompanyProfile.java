package com.mycompany.digitalconsultant.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidade central do Perfil da Empresa (Reutilizável e com compliance LGPD).
 */
public class CompanyProfile {

    private String id;
    private String nomeEmpresa;
    private String setorAtuacao;
    private PorteEmpresa porte;
    private Set<CanalMarketing> canais;
    private BigDecimal orcamentoMensal;
    private Set<MetaMarketing> metas;
    private boolean consentimentoLGPD;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataConsentimento;
    
    private String usuarioResponsavel;

    public CompanyProfile() {
        this.canais = new HashSet<>();
        this.metas = new HashSet<>();
    }

    public CompanyProfile(String id, String nomeEmpresa, String setorAtuacao, PorteEmpresa porte,
                          Set<CanalMarketing> canais, BigDecimal orcamentoMensal,
                          Set<MetaMarketing> metas, boolean consentimentoLGPD,
                          LocalDateTime dataConsentimento, String usuarioResponsavel) {
        this.id = id;
        this.nomeEmpresa = Objects.requireNonNull(nomeEmpresa, "Nome da empresa é obrigatório");
        this.setorAtuacao = setorAtuacao != null ? setorAtuacao : "Geral";
        this.porte = porte != null ? porte : PorteEmpresa.PEQUENA_MEDIA;
        this.canais = canais != null ? new HashSet<>(canais) : new HashSet<>();
        this.orcamentoMensal = orcamentoMensal != null ? orcamentoMensal : BigDecimal.ZERO;
        this.metas = metas != null ? new HashSet<>(metas) : new HashSet<>();
        this.consentimentoLGPD = consentimentoLGPD;
        this.dataConsentimento = dataConsentimento != null ? dataConsentimento : LocalDateTime.now();
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getSetorAtuacao() {
        return setorAtuacao;
    }

    public void setSetorAtuacao(String setorAtuacao) {
        this.setorAtuacao = setorAtuacao;
    }

    public PorteEmpresa getPorte() {
        return porte;
    }

    public void setPorte(PorteEmpresa porte) {
        this.porte = porte;
    }

    public Set<CanalMarketing> getCanais() {
        return Collections.unmodifiableSet(canais != null ? canais : Collections.emptySet());
    }

    public void setCanais(Set<CanalMarketing> canais) {
        this.canais = canais != null ? new HashSet<>(canais) : new HashSet<>();
    }

    public void addCanal(CanalMarketing canal) {
        if (this.canais == null) {
            this.canais = new HashSet<>();
        }
        this.canais.add(canal);
    }

    public BigDecimal getOrcamentoMensal() {
        return orcamentoMensal;
    }

    public void setOrcamentoMensal(BigDecimal orcamentoMensal) {
        this.orcamentoMensal = orcamentoMensal;
    }

    public Set<MetaMarketing> getMetas() {
        return Collections.unmodifiableSet(metas != null ? metas : Collections.emptySet());
    }

    public void setMetas(Set<MetaMarketing> metas) {
        this.metas = metas != null ? new HashSet<>(metas) : new HashSet<>();
    }

    public void addMeta(MetaMarketing meta) {
        if (this.metas == null) {
            this.metas = new HashSet<>();
        }
        this.metas.add(meta);
    }

    public boolean isConsentimentoLGPD() {
        return consentimentoLGPD;
    }

    public void setConsentimentoLGPD(boolean consentimentoLGPD) {
        this.consentimentoLGPD = consentimentoLGPD;
    }

    public LocalDateTime getDataConsentimento() {
        return dataConsentimento;
    }

    public void setDataConsentimento(LocalDateTime dataConsentimento) {
        this.dataConsentimento = dataConsentimento;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(String usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }
}
