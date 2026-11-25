package com.mycompany.digitalconsultant.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Perfil da Instituição Financeira / Cliente de BPO (Reutilizável e LGPD/Sigilo Bancário).
 */
public class FinancialProfile {

    private String id;
    private String nomeInstituicao;
    private TipoInstituicaoFinanceira tipo;
    private BigDecimal aumOuFaturamentoMensal;
    private String regimeTributario; // Lucro Real, Lucro Presumido, Simples Nacional
    private Set<ModuloBpo> modulosBpoAtivos;
    private Set<ObjetivoMercadoFinanceiro> objetivos;
    private boolean consentimentoLgpdSigilo;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataConsentimento;
    
    private String responsavelFinanceiro;

    public FinancialProfile() {
        this.modulosBpoAtivos = new HashSet<>();
        this.objetivos = new HashSet<>();
        this.dataConsentimento = LocalDateTime.now();
    }

    public FinancialProfile(String id, String nomeInstituicao, TipoInstituicaoFinanceira tipo,
                            BigDecimal aumOuFaturamentoMensal, String regimeTributario,
                            Set<ModuloBpo> modulosBpoAtivos, Set<ObjetivoMercadoFinanceiro> objetivos,
                            boolean consentimentoLgpdSigilo, String responsavelFinanceiro) {
        this.id = id;
        this.nomeInstituicao = Objects.requireNonNull(nomeInstituicao, "Nome da instituição é obrigatório");
        this.tipo = tipo != null ? tipo : TipoInstituicaoFinanceira.EMPRESA_CLIENTE_BPO;
        this.aumOuFaturamentoMensal = aumOuFaturamentoMensal != null ? aumOuFaturamentoMensal : BigDecimal.ZERO;
        this.regimeTributario = regimeTributario != null ? regimeTributario : "Lucro Real";
        this.modulosBpoAtivos = modulosBpoAtivos != null ? new HashSet<>(modulosBpoAtivos) : new HashSet<>();
        this.objetivos = objetivos != null ? new HashSet<>(objetivos) : new HashSet<>();
        this.consentimentoLgpdSigilo = consentimentoLgpdSigilo;
        this.dataConsentimento = LocalDateTime.now();
        this.responsavelFinanceiro = responsavelFinanceiro;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeInstituicao() {
        return nomeInstituicao;
    }

    public void setNomeInstituicao(String nomeInstituicao) {
        this.nomeInstituicao = nomeInstituicao;
    }

    public TipoInstituicaoFinanceira getTipo() {
        return tipo;
    }

    public void setTipo(TipoInstituicaoFinanceira tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getAumOuFaturamentoMensal() {
        return aumOuFaturamentoMensal;
    }

    public void setAumOuFaturamentoMensal(BigDecimal aumOuFaturamentoMensal) {
        this.aumOuFaturamentoMensal = aumOuFaturamentoMensal;
    }

    public String getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(String regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public Set<ModuloBpo> getModulosBpoAtivos() {
        return Collections.unmodifiableSet(modulosBpoAtivos != null ? modulosBpoAtivos : Collections.emptySet());
    }

    public void setModulosBpoAtivos(Set<ModuloBpo> modulosBpoAtivos) {
        this.modulosBpoAtivos = modulosBpoAtivos != null ? new HashSet<>(modulosBpoAtivos) : new HashSet<>();
    }

    public void addModuloBpo(ModuloBpo mod) {
        if (this.modulosBpoAtivos == null) this.modulosBpoAtivos = new HashSet<>();
        this.modulosBpoAtivos.add(mod);
    }

    public Set<ObjetivoMercadoFinanceiro> getObjetivos() {
        return Collections.unmodifiableSet(objetivos != null ? objetivos : Collections.emptySet());
    }

    public void setObjetivos(Set<ObjetivoMercadoFinanceiro> objetivos) {
        this.objetivos = objetivos != null ? new HashSet<>(objetivos) : new HashSet<>();
    }

    public void addObjetivo(ObjetivoMercadoFinanceiro obj) {
        if (this.objetivos == null) this.objetivos = new HashSet<>();
        this.objetivos.add(obj);
    }

    public boolean isConsentimentoLgpdSigilo() {
        return consentimentoLgpdSigilo;
    }

    public void setConsentimentoLgpdSigilo(boolean consentimentoLgpdSigilo) {
        this.consentimentoLgpdSigilo = consentimentoLgpdSigilo;
    }

    public LocalDateTime getDataConsentimento() {
        return dataConsentimento;
    }

    public void setDataConsentimento(LocalDateTime dataConsentimento) {
        this.dataConsentimento = dataConsentimento;
    }

    public String getResponsavelFinanceiro() {
        return responsavelFinanceiro;
    }

    public void setResponsavelFinanceiro(String responsavelFinanceiro) {
        this.responsavelFinanceiro = responsavelFinanceiro;
    }
}
