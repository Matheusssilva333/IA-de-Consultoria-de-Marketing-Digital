package com.mycompany.digitalconsultant.rag;

import com.mycompany.digitalconsultant.domain.model.CanalMarketing;
import com.mycompany.digitalconsultant.domain.model.MetaMarketing;
import com.mycompany.digitalconsultant.domain.model.PorteEmpresa;

import java.util.ArrayList;
import java.util.List;

/**
 * Base de Conhecimento Estruturada de Marketing Digital (RAG Corporativo - Etapa 4).
 * Contém frameworks rigorosos: AARRR (Pirate Metrics), Funil de Vendas e Benchmarks setoriais.
 */
public class MarketingKnowledgeBase {

    public record KnowledgeFragment(String tag, String categoria, String conteudo) {}

    private final List<KnowledgeFragment> fragments = new ArrayList<>();

    public MarketingKnowledgeBase() {
        carregarFrameworkAARRR();
        carregarFrameworkFunil();
        carregarBenchmarksSetoriais();
        carregarMelhoresPraticasCanais();
    }

    private void carregarFrameworkAARRR() {
        fragments.add(new KnowledgeFragment(
                "AARRR_AQUISICAO", "Framework AARRR",
                "AQUISIÇÃO (Acquisition): Como novos clientes encontram a marca. " +
                "Métricas-chave: CAC (Custo de Aquisição de Clientes), CTR, Taxa de Cliques, CPC e Tráfego Qualificado. " +
                "Estratégia: Equilibrar canais pagos (Google Ads para intenção de busca, Meta Ads para interesse) " +
                "com canais orgânicos (SEO e Marketing de Conteúdo de autoridade)."
        ));
        fragments.add(new KnowledgeFragment(
                "AARRR_ATIVACAO", "Framework AARRR",
                "ATIVAÇÃO (Activation): A primeira experiência de valor real do cliente ('Aha! Moment'). " +
                "Métricas-chave: Taxa de conversão de landing page (benchmark saudável: 3% a 7%), tempo até o primeiro valor, taxa de onboarding. " +
                "Estratégia: Simplificar formulários, eliminar atritos no checkout e usar copywriting focado na resolução imediata da dor."
        ));
        fragments.add(new KnowledgeFragment(
                "AARRR_RETENCAO", "Framework AARRR",
                "RETENÇÃO (Retention): Manter o cliente comprando ou ativo no ciclo de vida. " +
                "Métricas-chave: Churn Rate (taxa de cancelamento/perda), LTV (Lifetime Value), Taxa de Recompra. " +
                "Regra de Ouro: Reter clientes custa de 5x a 7x menos do que adquirir novos. Automação de e-mail e pós-venda via WhatsApp são cruciais."
        ));
        fragments.add(new KnowledgeFragment(
                "AARRR_RECEITA", "Framework AARRR",
                "RECEITA (Revenue): Monetização eficiente e margem por cliente. " +
                "Métricas-chave: LTV/CAC Ratio (alvo ideal > 3:1), Ticket Médio, Margem de Contribuição, ROAS mínimo viável (geralmente > 3.5x). " +
                "Estratégia: Ofertas de order bump, upsell, cross-sell e planos anuais com desconto."
        ));
        fragments.add(new KnowledgeFragment(
                "AARRR_RECOMENDACAO", "Framework AARRR",
                "RECOMENDAÇÃO (Referral): Clientes transformados em promotores ativos da marca. " +
                "Métricas-chave: NPS (Net Promoter Score), Coeficiente Viral (K-factor > 1), percentual de vendas vindas de indicação. " +
                "Estratégia: Programas de 'Indique e Ganhe', incentivos de crédito e parcerias estratégicas."
        ));
    }

    private void carregarFrameworkFunil() {
        fragments.add(new KnowledgeFragment(
                "FUNIL_TOFU", "Funil de Vendas",
                "TOPO DE FUNIL (ToFu - Atração & Consciência): Público frio descobrindo o problema. " +
                "Conteúdo: Educativo, reels, posts informativos, blogs de resposta direta. " +
                "Objetivo: Gerar atenção e construir audiência proprietária (pixel, listas)."
        ));
        fragments.add(new KnowledgeFragment(
                "FUNIL_MOFU", "Funil de Vendas",
                "MEIO DE FUNIL (MoFu - Consideração & Nutrição): Público morno comparando soluções. " +
                "Conteúdo: Estudos de caso, webinars, planilhas gratuitas, demonstrações práticas, sequências de e-mail marketing. " +
                "Objetivo: Converter visitantes em leads qualificados (MQLs)."
        ));
        fragments.add(new KnowledgeFragment(
                "FUNIL_BOFU", "Funil de Vendas",
                "FUNDO DE FUNIL (BoFu - Decisão & Fechamento): Público quente pronto para comprar. " +
                "Conteúdo: Prova social, depoimentos, garantias incondicionais, ofertas diretas, atendimento humano via WhatsApp. " +
                "Objetivo: Maximizar taxa de fechamento (SQL para Venda)."
        ));
    }

    private void carregarBenchmarksSetoriais() {
        fragments.add(new KnowledgeFragment(
                "BENCHMARK_ECOMMERCE", "Benchmarks de Mercado",
                "BENCHMARK E-COMMERCE: Taxa média de conversão em e-commerce no Brasil: 1.2% a 2.5%. " +
                "ROAS saudável em Meta Ads: 4.0x a 6.0x. Taxa de abandono de carrinho: 70% (recuperação com WhatsApp e e-mail recupera até 15%)."
        ));
        fragments.add(new KnowledgeFragment(
                "BENCHMARK_B2B_SERVICOS", "Benchmarks de Mercado",
                "BENCHMARK B2B & SERVIÇOS: Ciclo de venda médio: 15 a 60 dias. " +
                "Taxa de conversão de Lead para Oportunidade: 12% a 25%. Oportunidade para Cliente: 20% a 35%. " +
                "LinkedIn Ads + Outbound + Google Ads Search geram os leads de maior ticket médio."
        ));
        fragments.add(new KnowledgeFragment(
                "BENCHMARK_PME_LOCAL", "Benchmarks de Mercado",
                "BENCHMARK NEGÓCIOS LOCAIS / PME: Google Perfil de Empresa (Google Meu Negócio) gera até 40% das conversões gratuitas. " +
                "Anúncios com clique direto para WhatsApp geram leads imediatos a custo 30% menor que formulários extensos."
        ));
    }

    private void carregarMelhoresPraticasCanais() {
        fragments.add(new KnowledgeFragment(
                "CANAIS_PAID_MEDIA", "Estratégia de Canais",
                "MÍDIA PAGA: Google Ads captura demanda ativa (quem já está procurando comprar). " +
                "Meta Ads (Instagram/Facebook) gera nova demanda através de criativos de alto impacto e dores latentes."
        ));
        fragments.add(new KnowledgeFragment(
                "CANAIS_OWNED_MEDIA", "Estratégia de Canais",
                "CANAIS PROPRIETÁRIOS: E-mail e lista de contatos no WhatsApp são ativos anticrise imunes às flutuações de algoritmo das redes."
        ));
    }

    public List<KnowledgeFragment> getFragments() {
        return fragments;
    }
}
