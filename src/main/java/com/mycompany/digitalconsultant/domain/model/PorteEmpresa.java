package com.mycompany.digitalconsultant.domain.model;

public enum PorteEmpresa {
    MEI("Microempreendedor Individual (MEI)"),
    STARTUP("Startup / Early Stage"),
    PEQUENA_MEDIA("Pequena ou Média Empresa (PME)"),
    GRANDE("Grande Corporação / Enterprise");

    private final String descricao;

    PorteEmpresa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
