package com.mycompany.digitalconsultant.rag;

import com.mycompany.digitalconsultant.domain.model.CompanyProfile;
import com.mycompany.digitalconsultant.domain.model.PorteEmpresa;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Motor RAG (Retrieval-Augmented Generation) de Marketing.
 * Seleciona cirurgicamente fragmentos da base de conhecimento de acordo com o contexto da empresa.
 */
public class MarketingRagEngine {

    private final MarketingKnowledgeBase knowledgeBase = new MarketingKnowledgeBase();

    public String retrieveEnrichedContext(CompanyProfile profile, String userQuery) {
        StringBuilder context = new StringBuilder();
        context.append("--- CONTEXTO ESTRATÉGICO DE MARKETING (RAG KNOWLEDGE INJECTION) ---\n");

        List<MarketingKnowledgeBase.KnowledgeFragment> all = knowledgeBase.getFragments();

        // Sempre inclui os pilares do framework AARRR e do Funil
        List<MarketingKnowledgeBase.KnowledgeFragment> selected = all.stream()
                .filter(f -> f.categoria().equals("Framework AARRR") || f.categoria().equals("Funil de Vendas"))
                .collect(Collectors.toList());

        // Injeção de Benchmarks específicos
        if (profile != null) {
            if (profile.getPorte() == PorteEmpresa.MEI || profile.getPorte() == PorteEmpresa.PEQUENA_MEDIA) {
                all.stream().filter(f -> f.tag().contains("PME") || f.tag().contains("LOCAL")).forEach(selected::add);
            }
            if (profile.getSetorAtuacao() != null && profile.getSetorAtuacao().toLowerCase().contains("commerce")) {
                all.stream().filter(f -> f.tag().contains("ECOMMERCE")).forEach(selected::add);
            } else {
                all.stream().filter(f -> f.tag().contains("B2B")).forEach(selected::add);
            }
        }

        // Adiciona melhores práticas de canais
        all.stream().filter(f -> f.categoria().equals("Estratégia de Canais")).forEach(selected::add);

        // Monta o bloco de contexto
        for (MarketingKnowledgeBase.KnowledgeFragment frag : selected.stream().distinct().toList()) {
            context.append(String.format("[%s - %s]: %s\n", frag.categoria(), frag.tag(), frag.conteudo()));
        }

        context.append("------------------------------------------------------------------\n");
        return context.toString();
    }
}
