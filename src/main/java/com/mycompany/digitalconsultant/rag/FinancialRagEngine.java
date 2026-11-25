package com.mycompany.digitalconsultant.rag;

import com.mycompany.digitalconsultant.domain.model.FinancialProfile;

import java.util.List;

/**
 * Motor RAG Financeiro com Injeção Estratégica e Regra Estrita de Tradução Simples para Negócios.
 */
public class FinancialRagEngine {

    private final FinancialKnowledgeBase knowledgeBase = new FinancialKnowledgeBase();

    public String retrieveFinancialContext(FinancialProfile profile, String userQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- INTELIGÊNCIA DE MERCADO FINANCEIRO & BPO (RAG FINANCEIRO) ---\n");

        List<FinancialKnowledgeBase.KnowledgeFragment> all = knowledgeBase.getFragments();

        for (FinancialKnowledgeBase.KnowledgeFragment frag : all) {
            sb.append(String.format("[%s - %s]: %s\nTRADUÇÃO SIMPLES PARA NEGÓCIOS: %s\n\n",
                    frag.categoria(), frag.tag(), frag.conteudo(), frag.traducaoSimples()));
        }

        sb.append("DIRETRIZ DE VOZ OBRIGATÓRIA: Explique SEMPRE tudo em linguagem clara e acessível, " +
                "traduzindo qualquer termo técnico para lucro líquido, redução de custos em reais, captação de AUM ou liquidez em caixa.\n");
        sb.append("-----------------------------------------------------------------\n");
        return sb.toString();
    }
}
