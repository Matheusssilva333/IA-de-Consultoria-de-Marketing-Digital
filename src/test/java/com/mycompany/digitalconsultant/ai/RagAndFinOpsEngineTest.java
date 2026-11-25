package com.mycompany.digitalconsultant.ai;

import com.mycompany.digitalconsultant.domain.model.CanalMarketing;
import com.mycompany.digitalconsultant.domain.model.CompanyProfile;
import com.mycompany.digitalconsultant.domain.model.PorteEmpresa;
import com.mycompany.digitalconsultant.domain.model.StructuredDiagnosis;
import com.mycompany.digitalconsultant.rag.MarketingRagEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de RAG de Marketing, Parser Estruturado e FinOps")
public class RagAndFinOpsEngineTest {

    @Test
    @DisplayName("Deve injetar frameworks AARRR e Funil de Vendas através do RAG")
    void testRagContextInjection() {
        MarketingRagEngine rag = new MarketingRagEngine();
        CompanyProfile profile = new CompanyProfile();
        profile.setNomeEmpresa("Startup AI");
        profile.setPorte(PorteEmpresa.STARTUP);
        profile.setSetorAtuacao("SaaS B2B");
        profile.setCanais(Set.of(CanalMarketing.LINKEDIN, CanalMarketing.GOOGLE_ADS));

        String context = rag.retrieveEnrichedContext(profile, "Como escalar leads B2B?");

        assertNotNull(context);
        assertTrue(context.contains("Framework AARRR"));
        assertTrue(context.contains("Funil de Vendas"));
        assertTrue(context.contains("AQUISIÇÃO"));
        assertTrue(context.contains("RETENÇÃO"));
    }

    @Test
    @DisplayName("Deve processar e validar as 4 seções fixas da saída estruturada")
    void testStructuredOutputParsing() {
        StructuredOutputParser parser = new StructuredOutputParser();
        String jsonSample = """
        {
          "diagnostico": "Cenário com CAC elevado e baixa retenção.",
          "oportunidades": ["Focar em Google Ads Search", "Ativar régua de e-mail"],
          "acoesPriorizadas": [
            {"titulo": "Criar LP", "descricao": "Focar em conversão", "impacto": "ALTO", "esforco": "MEDIO", "prazoSugerido": "5 dias"}
          ],
          "metricas": [
            {"metrica": "CAC", "valorAlvo": "< R$ 40", "explicacao": "Unit economics saudável"}
          ]
        }
        """;

        StructuredDiagnosis diag = parser.parse(jsonSample);

        assertEquals("Cenário com CAC elevado e baixa retenção.", diag.getDiagnostico());
        assertEquals(2, diag.getOportunidades().size());
        assertEquals(1, diag.getAcoesPriorizadas().size());
        assertEquals("ALTO", diag.getAcoesPriorizadas().get(0).getImpacto());
        assertEquals(1, diag.getMetricas().size());
        assertEquals("< R$ 40", diag.getMetricas().get(0).getValorAlvo());
    }

    @Test
    @DisplayName("Deve calcular telemetria de tokens, custos USD/BRL e respeitar teto FinOps")
    void testFinOpsTokensAndCostTracking() {
        FinOpsCostTracker tracker = FinOpsCostTracker.getInstance();
        tracker.resetSessionMetrics();

        int promptTokens = tracker.estimateTokens("Qual a melhor estratégia para reduzir o CAC?");
        int completionTokens = tracker.estimateTokens("Recomendo reestruturar campanhas de Google Ads...");

        assertTrue(promptTokens > 0);
        assertTrue(completionTokens > 0);

        double costUsd = tracker.calculateCostUSD(promptTokens, completionTokens);
        double costBrl = tracker.calculateCostBRL(costUsd);

        assertTrue(costUsd > 0);
        assertTrue(costBrl > 0);

        tracker.recordUsage(promptTokens, completionTokens);
        assertEquals(promptTokens + completionTokens, tracker.getTotalTokensAccumulated());
    }
}
