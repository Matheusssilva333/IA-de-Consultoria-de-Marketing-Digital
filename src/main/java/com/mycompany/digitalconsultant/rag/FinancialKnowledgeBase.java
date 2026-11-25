package com.mycompany.digitalconsultant.rag;

import java.util.ArrayList;
import java.util.List;

/**
 * Base de Conhecimento RAG Especializada no Mercado Financeiro, Gestão de AUM e BPO Financeiro.
 * Foco estrito: Tradução de qualquer gargalo para impacto financeiro direto (Caixa, AUM, CDI, Margem).
 */
public class FinancialKnowledgeBase {

    public record KnowledgeFragment(String tag, String categoria, String conteudo, String traducaoSimples) {}

    private final List<KnowledgeFragment> fragments = new ArrayList<>();

    public FinancialKnowledgeBase() {
        carregarFrameworksAumEMercado();
        carregarFrameworksBpoFinanceiro();
        carregarIndicadoresRentabilidadeTesouraria();
        carregarComplianceBacenCvm();
    }

    private void carregarFrameworksAumEMercado() {
        fragments.add(new KnowledgeFragment(
                "AUM_CAPTACAO_NNM", "Mercado Financeiro & Wealth",
                "CAPTAÇÃO LÍQUIDA (Net New Money - NNM): O sangue de gestoras, family offices e assessorias de investimento. " +
                "Métricas: Captação Bruta - Resgates = NNM. CAC de Captação vs LTV do Cotista. " +
                "Estratégia: Alocar assessores nos nichos de alta renda com esteira de relacionamento e alocação orientada por Perfil de Risco (Suitability).",
                "Em termos de negócio: É o dinheiro novo que entra menos o dinheiro que sai. Se o resgate for maior que o aporte, o negócio encolhe."
        ));
        fragments.add(new KnowledgeFragment(
                "AUM_RETENCAO_CHURN", "Mercado Financeiro & Wealth",
                "RETENÇÃO DE PATRIMÔNIO (Wealth Retention): Preservação de AUM em momentos de volatilidade de mercado. " +
                "Métricas: Taxa de Churn de Patrimônio, Volume sob Custódia, Share of Wallet do Investidor. " +
                "Estratégia: Relatórios de rentabilidade transparentes em D+1, comunicação proativa em quedas de bolsa e diversificação internacional.",
                "Em termos de negócio: Proteger o dinheiro do cliente para que ele não saque na primeira oscilação da bolsa."
        ));
    }

    private void carregarFrameworksBpoFinanceiro() {
        fragments.add(new KnowledgeFragment(
                "BPO_CONCILIACAO_FECHAMENTO", "BPO Financeiro",
                "CONCILIAÇÃO BANCÁRIA DIÁRIA (D+1): Confronto sistemático entre extratos bancários, adquirentes de cartão e livros fiscais. " +
                "Elimina furos de caixa, evita pagamento de juros/multas por boletos esquecidos e garante fidedignidade do balancete.",
                "Em termos de negócio: Garantir que cada centavo que entrou ou saiu da conta bancária bate perfeitamente com a contabilidade, sem dinheiro sumindo."
        ));
        fragments.add(new KnowledgeFragment(
                "BPO_FLUXO_CAIXA_RUNWAY", "BPO Financeiro",
                "FLUXO DE CAIXA OPERACIONAL & RUNWAY: Projeção de entradas e saídas operacionais para 30, 60 e 90 dias. " +
                "Identificação antecipada do Ponto de Queima de Caixa (Burn Rate) e necessidade de Capital de Giro (Working Capital).",
                "Em termos de negócio: Saber com certeza quanto tempo a empresa sobrevive com o caixa atual antes de precisar de empréstimo ou aporte."
        ));
        fragments.add(new KnowledgeFragment(
                "BPO_PMR_PMP_CICLO", "BPO Financeiro",
                "CICLO FINANCEIRO (PMR vs PMP): Redução do Prazo Médio de Recebimento (PMR) e alongamento do Prazo Médio de Pagamento (PMP). " +
                "Aumentar o fôlego financeiro sem recorrer a antecipação de recebíveis a taxas abusivas.",
                "Em termos de negócio: Receber dos clientes antes de ter que pagar os fornecedores, mantendo o caixa sempre positivo e rendendo."
        ));
    }

    private void carregarIndicadoresRentabilidadeTesouraria() {
        fragments.add(new KnowledgeFragment(
                "TESOURARIA_CAIXA_LIVRE_CDI", "Tesouraria & FinOps",
                "RENTABILIZAÇÃO DE CAIXA LIVRE: Manter o caixa ocioso 100% aplicado em operações compromissadas diárias a 100%+ do CDI. " +
                "Dinheiro parado na conta corrente sem render representa custo de oportunidade e queima de valor pela inflação.",
                "Em termos de negócio: Dinheiro parado em conta corrente é prejuízo. Deve estar rendendo juros compostos a cada segundo."
        ));
        fragments.add(new KnowledgeFragment(
                "INDICADOR_DRE_EBITDA", "DRE & Performance",
                "DRE GERENCIAL & EBITDA: Demonstração do Resultado do Exercício com separação de custos fixos, variáveis e despesas financeiras. " +
                "Cálculo do lucro antes de juros, impostos, depreciação e amortização para mensurar a real capacidade geradora de caixa operacional.",
                "Em termos de negócio: A prova real de quanto a sua operação comercial realmente lucra, tirando o barulho de impostos e juros de banco."
        ));
    }

    private void carregarComplianceBacenCvm() {
        fragments.add(new KnowledgeFragment(
                "COMPLIANCE_SIGILO_REGULATORIO", "Compliance Financeiro",
                "SIGILO BANCÁRIO & COMPLIANCE REGULATÓRIO (LC 105/01 & LGPD): Proteção irrestrita de dados transacionais, extratos e patrimônio de cotistas. " +
                "Criptografia ponta a ponta e auditoria de acesso obrigatórias por órgãos reguladores (Bacen, CVM e Anbima).",
                "Em termos de negócio: Blindar o sigilo do dinheiro e da vida financeira do cliente para evitar multas regulatórias pesadas e processos."
        ));
    }

    public List<KnowledgeFragment> getFragments() {
        return fragments;
    }
}
