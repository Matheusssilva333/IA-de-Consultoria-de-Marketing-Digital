package com.mycompany.digitalconsultant.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Item de Transação Financeira de BPO (Contas a Pagar, Contas a Receber, Conciliação).
 */
public class FinancialTransactionItem {

    private String id;
    private LocalDate dataCompetencia;
    private String tipo; // RECEITA, DESPESA_OPERACIONAL, TRIBUTO, INVESTIMENTO
    private String descricao;
    private BigDecimal valor;
    private String contraparte; // Cliente ou Fornecedor
    private boolean conciliado;

    public FinancialTransactionItem() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.dataCompetencia = LocalDate.now();
        this.valor = BigDecimal.ZERO;
        this.conciliado = false;
    }

    public FinancialTransactionItem(LocalDate dataCompetencia, String tipo, String descricao,
                                  BigDecimal valor, String contraparte, boolean conciliado) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.dataCompetencia = dataCompetencia;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.contraparte = contraparte;
        this.conciliado = conciliado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDataCompetencia() {
        return dataCompetencia;
    }

    public void setDataCompetencia(LocalDate dataCompetencia) {
        this.dataCompetencia = dataCompetencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getContraparte() {
        return contraparte;
    }

    public void setContraparte(String contraparte) {
        this.contraparte = contraparte;
    }

    public boolean isConciliado() {
        return conciliado;
    }

    public void setConciliado(boolean conciliado) {
        this.conciliado = conciliado;
    }
}
