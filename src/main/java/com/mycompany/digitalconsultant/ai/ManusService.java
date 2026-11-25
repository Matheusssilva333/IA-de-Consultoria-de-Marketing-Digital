package com.mycompany.digitalconsultant.ai;

import com.mycompany.digitalconsultant.bpo.BpoProcessingService;
import com.mycompany.digitalconsultant.datamesh.domain.BpoOperationsProduct;
import com.mycompany.digitalconsultant.datamesh.domain.FinOpsTelemetryProduct;
import com.mycompany.digitalconsultant.datamesh.domain.FinancialProfileProduct;
import com.mycompany.digitalconsultant.datamesh.domain.MarketingIntelligenceProduct;
import com.mycompany.digitalconsultant.datamesh.governance.DataMeshFederationRegistry;
import com.mycompany.digitalconsultant.domain.model.*;
import com.mycompany.digitalconsultant.rag.FinancialRagEngine;
import com.mycompany.digitalconsultant.rag.MarketingRagEngine;
import com.mycompany.digitalconsultant.security.LgpdComplianceService;
import com.mycompany.digitalconsultant.security.SecuritySanitizer;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço Orquestrador de Consultoria para o Mercado Financeiro & BPO com IA.
 * Regra Soberana: SEMPRE explicar de forma simples e clara, traduzindo termos técnicos para impacto financeiro real.
 */
public class ManusService {

    private final ManusClient client = new ManusClient();
    private final MarketingRagEngine marketingRag = new MarketingRagEngine();
    private final FinancialRagEngine financialRag = new FinancialRagEngine();
    private final StructuredOutputParser parser = new StructuredOutputParser();
    private final FinOpsCostTracker costTracker = FinOpsCostTracker.getInstance();
    private final LgpdComplianceService lgpdService = LgpdComplianceService.getInstance();
    private final DataMeshFederationRegistry dataMesh = DataMeshFederationRegistry.getInstance();
    private final BpoProcessingService bpoService = new BpoProcessingService();

    /**
     * Consultoria Soberana para Mercado Financeiro e BPO.
     */
    public StructuredDiagnosis performFinancialConsultation(FinancialProfile profile, String userQuery, BpoDashboardSummary bpoSummary) {
        if (profile != null && !profile.isConsentimentoLgpdSigilo()) {
            throw new SecurityException("Operação bloqueada por governança de Sigilo Bancário & LGPD.");
        }

        String sanitizedQuery = SecuritySanitizer.sanitizeInput(userQuery);
        String prompt = buildFinancialPrompt(profile, sanitizedQuery, bpoSummary);

        ManusClient.ClientResponse clientResponse = client.askWithTelemetry(prompt);

        StructuredDiagnosis diagnosis = parser.parse(clientResponse.rawBody());
        diagnosis.setPromptTokens(clientResponse.promptTokens());
        diagnosis.setCompletionTokens(clientResponse.completionTokens());
        diagnosis.setTotalTokens(clientResponse.promptTokens() + clientResponse.completionTokens());
        diagnosis.setCustoEstimadoUSD(costTracker.calculateCostUSD(clientResponse.promptTokens(), clientResponse.completionTokens()));
        diagnosis.setLatenciaMs(clientResponse.latencyMs());

        // Publicação Federada no Data Mesh Financeiro
        try {
            if (profile != null) {
                FinancialProfileProduct profileProduct = new FinancialProfileProduct(profile);
                dataMesh.publishAndVerify(profileProduct);
            }

            if (bpoSummary != null) {
                BpoOperationsProduct bpoProduct = new BpoOperationsProduct(UUID.randomUUID().toString().substring(0, 8), bpoSummary);
                dataMesh.publishAndVerify(bpoProduct);
            }

            MarketingIntelligenceProduct intelProduct = new MarketingIntelligenceProduct(diagnosis);
            dataMesh.publishAndVerify(intelProduct);

            FinOpsTelemetryProduct finOpsProduct = new FinOpsTelemetryProduct(new FinOpsTelemetryProduct.TelemetryRecord(
                    UUID.randomUUID().toString(),
                    profile != null ? profile.getResponsavelFinanceiro() : "fin_operador",
                    clientResponse.promptTokens(),
                    clientResponse.completionTokens(),
                    diagnosis.getTotalTokens(),
                    diagnosis.getCustoEstimadoUSD(),
                    clientResponse.latencyMs(),
                    true,
                    "FINANCIAL_BPO_EXECUTION"
            ));
            dataMesh.publishAndVerify(finOpsProduct);
        } catch (Exception e) {
            System.err.println("[DataMesh Telemetry Warning] " + e.getMessage());
        }

        return diagnosis;
    }

    private String buildFinancialPrompt(FinancialProfile profile, String userQuery, BpoDashboardSummary bpoSummary) {
        StringBuilder sb = new StringBuilder();
        sb.append("Você é o Consultor Sênior Estratégico do Mercado Financeiro e BPO (SAO System v2.0).\n");
        sb.append("DIRETRIZ SUPREMA: Você DEVE SEMPRE explicar tudo de forma simples, direta e cristalina, ");
        sb.append("SEMPRE traduzindo qualquer conceito técnico (tecnologia, algoritmos, processos de BPO) para impacto prático no caixa, ");
        sb.append("lucro líquido, rentabilidade sobre CDI, captação de AUM ou mitigação de inadimplência.\n\n");

        sb.append("Retorne ESTRITAMENTE um JSON com as 4 seções fixas:\n");
        sb.append("1. 'diagnostico': Análise clara da saúde financeira, gargalos de conciliação ou liquidez, traduzidos para a realidade do tomador de decisão.\n");
        sb.append("2. 'oportunidades': Lista de alavancas de ganho de caixa, redução de queima de caixa (burn rate) e eficiência operacional.\n");
        sb.append("3. 'acoesPriorizadas': Ações com 'titulo', 'descricao', 'impacto' (ALTO/MEDIO/BAIXO), 'esforco' (ALTO/MEDIO/BAIXO) e 'prazoSugerido'.\n");
        sb.append("4. 'metricas': KPIs com 'metrica', 'valorAlvo' e 'explicacao' (explicando em palavras simples o significado financeiro).\n\n");

        if (profile != null) {
            sb.append("=== PERFIL DA INSTITUIÇÃO FINANCEIRA / CLIENTE BPO ===\n");
            sb.append("Instituição: ").append(profile.getNomeInstituicao()).append("\n");
            sb.append("Tipo: ").append(profile.getTipo().getDescricao()).append("\n");
            sb.append("AUM / Faturamento Mensal: R$ ").append(profile.getAumOuFaturamentoMensal()).append("\n");
            sb.append("Regime Tributário: ").append(profile.getRegimeTributario()).append("\n");
            sb.append("Módulos BPO Ativos: ").append(profile.getModulosBpoAtivos().stream().map(ModuloBpo::getDescricao).collect(Collectors.joining(", "))).append("\n");
            sb.append("Objetivos Estratégicos: ").append(profile.getObjetivos().stream().map(ObjetivoMercadoFinanceiro::getDescricao).collect(Collectors.joining(", "))).append("\n\n");
        }

        if (bpoSummary != null) {
            sb.append("=== DADOS OPERACIONAIS DE BPO FINANCEIRO (LANÇAMENTOS E CONCILIAÇÃO) ===\n");
            sb.append("Total de Receitas: R$ ").append(bpoSummary.totalReceitas()).append("\n");
            sb.append("Total de Despesas: R$ ").append(bpoSummary.totalDespesas()).append("\n");
            sb.append("Saldo Líquido Operacional: R$ ").append(bpoSummary.saldoLiquido()).append("\n");
            sb.append("Volume Aguardando Conciliação: R$ ").append(bpoSummary.totalAguardandoConciliacao()).append("\n");
            sb.append("Taxa de Conciliação em D+1: ").append(bpoSummary.taxaConciliacaoPercent()).append("%\n\n");
        }

        // Injeção do RAG Financeiro
        sb.append(financialRag.retrieveFinancialContext(profile, userQuery)).append("\n");

        sb.append("=== DESAFIO / PERGUNTA DO EXECUTIVO FINANCEIRO ===\n");
        sb.append(userQuery).append("\n\n");
        sb.append("IMPORTANTE: Responda apenas com o JSON estruturado.");

        return sb.toString();
    }

    /**
     * Mantém compatibilidade com fluxo anterior de Marketing
     */
    public StructuredDiagnosis performConsultation(CompanyProfile profile, String userQuery) {
        if (profile != null && !lgpdService.validateConsent(profile)) {
            throw new SecurityException("Operação bloqueada por governança LGPD: Consentimento não registrado.");
        }
        String sanitizedQuery = SecuritySanitizer.sanitizeInput(userQuery);
        String prompt = buildMarketingPrompt(profile, sanitizedQuery);
        ManusClient.ClientResponse clientResponse = client.askWithTelemetry(prompt);

        StructuredDiagnosis diagnosis = parser.parse(clientResponse.rawBody());
        diagnosis.setPromptTokens(clientResponse.promptTokens());
        diagnosis.setCompletionTokens(clientResponse.completionTokens());
        diagnosis.setTotalTokens(clientResponse.promptTokens() + clientResponse.completionTokens());
        diagnosis.setCustoEstimadoUSD(costTracker.calculateCostUSD(clientResponse.promptTokens(), clientResponse.completionTokens()));
        diagnosis.setLatenciaMs(clientResponse.latencyMs());

        return diagnosis;
    }

    private String buildMarketingPrompt(CompanyProfile profile, String userQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append("Você é um Consultor Sênior de Crescimento Empresarial e Mercado Financeiro.\n");
        sb.append("SEMPRE explique tudo em termos simples e claros de negócios.\n");
        sb.append("Retorne estritamente um JSON estruturado com 'diagnostico', 'oportunidades', 'acoesPriorizadas' e 'metricas'.\n\n");
        if (profile != null) {
            sb.append("Empresa: ").append(profile.getNomeEmpresa()).append(" | Orçamento: R$ ").append(profile.getOrcamentoMensal()).append("\n");
        }
        sb.append(marketingRag.retrieveEnrichedContext(profile, userQuery)).append("\n");
        sb.append("Pergunta: ").append(userQuery);
        return sb.toString();
    }

    public String askConsultant(String prompt) {
        try {
            StructuredDiagnosis diag = performFinancialConsultation(null, prompt, null);
            return String.format("""
                    [DIAGNÓSTICO FINANCEIRO & BPO]
                    %s
                    
                    [OPORTUNIDADES DE CAIXA E RENTABILIDADE]
                    - %s
                    
                    [PLANO DE AÇÃO EM TERMOS SIMPLES DE NEGÓCIO]
                    %s
                    
                    [INDICADORES DE SUCESSO (KPIS)]
                    %s
                    """,
                    diag.getDiagnostico(),
                    String.join("\n- ", diag.getOportunidades()),
                    diag.getAcoesPriorizadas().stream().map(a -> "• [" + a.getImpacto() + "] " + a.getTitulo() + " -> " + a.getDescricao() + " (Prazo: " + a.getPrazoSugerido() + ")").collect(Collectors.joining("\n")),
                    diag.getMetricas().stream().map(m -> "• " + m.getMetrica() + ": " + m.getValorAlvo() + " (" + m.getExplicacao() + ")").collect(Collectors.joining("\n"))
            );
        } catch (Exception e) {
            return "Erro ao processar consultoria: " + e.getMessage();
        }
    }
}
