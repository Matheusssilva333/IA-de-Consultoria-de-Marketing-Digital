package com.mycompany.digitalconsultant.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.digitalconsultant.domain.model.ActionItem;
import com.mycompany.digitalconsultant.domain.model.MetricTarget;
import com.mycompany.digitalconsultant.domain.model.StructuredDiagnosis;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser resiliente para a Saída Estruturada (Etapa 3 do fluxograma).
 * Garante as seções fixas: Diagnóstico, Oportunidades, Ações Priorizadas, Métricas.
 */
public class StructuredOutputParser {

    private static final Logger LOGGER = Logger.getLogger(StructuredOutputParser.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("(?s)```(?:json)?\\s*(\\{.*?\\})\\s*```");

    public StructuredDiagnosis parse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return createFallbackDiagnosis("Resposta vazia da IA.");
        }

        try {
            String jsonContent = extractJson(rawResponse);
            JsonNode root = MAPPER.readTree(jsonContent);

            StructuredDiagnosis diagnosis = new StructuredDiagnosis();

            // 1. Diagnóstico
            if (root.has("diagnostico") && !root.get("diagnostico").isNull()) {
                diagnosis.setDiagnostico(root.get("diagnostico").asText());
            } else {
                diagnosis.setDiagnostico("Diagnóstico estratégico gerado a partir do contexto da empresa.");
            }

            // 2. Oportunidades
            List<String> oportunidades = new ArrayList<>();
            if (root.has("oportunidades") && root.get("oportunidades").isArray()) {
                for (JsonNode opNode : root.get("oportunidades")) {
                    oportunidades.add(opNode.asText());
                }
            }
            if (oportunidades.isEmpty()) {
                oportunidades.add("Otimização de canais com maior intenção de conversão imediata.");
                oportunidades.add("Estruturação de funil de ativação para reduzir churn e aumentar LTV.");
            }
            diagnosis.setOportunidades(oportunidades);

            // 3. Ações Priorizadas
            List<ActionItem> acoes = new ArrayList<>();
            if (root.has("acoesPriorizadas") && root.get("acoesPriorizadas").isArray()) {
                for (JsonNode acNode : root.get("acoesPriorizadas")) {
                    String titulo = acNode.has("titulo") ? acNode.get("titulo").asText() : "Ação de Marketing";
                    String descricao = acNode.has("descricao") ? acNode.get("descricao").asText() : "";
                    String impacto = acNode.has("impacto") ? acNode.get("impacto").asText() : "ALTO";
                    String esforco = acNode.has("esforco") ? acNode.get("esforco").asText() : "MEDIO";
                    String prazo = acNode.has("prazoSugerido") ? acNode.get("prazoSugerido").asText() : "14 dias";
                    acoes.add(new ActionItem(titulo, descricao, impacto, esforco, prazo));
                }
            }
            if (acoes.isEmpty()) {
                acoes.add(new ActionItem("Criar Landing Page de Alta Conversão", "Estruturar proposta única de valor com prova social", "ALTO", "MEDIO", "7 dias"));
                acoes.add(new ActionItem("Configurar Rastreamento de Conversões (Pixel & API)", "Garantir atribuição precisa de tráfego e cálculo de CAC", "ALTO", "BAIXO", "3 dias"));
                acoes.add(new ActionItem("Implementar Régua de E-mail de Boas-Vindas", "Ativação automática de novos leads e recuperação de carrinho", "MEDIO", "BAIXO", "5 dias"));
            }
            diagnosis.setAcoesPriorizadas(acoes);

            // 4. Métricas
            List<MetricTarget> metricas = new ArrayList<>();
            if (root.has("metricas") && root.get("metricas").isArray()) {
                for (JsonNode mNode : root.get("metricas")) {
                    String metrica = mNode.has("metrica") ? mNode.get("metrica").asText() : "KPI";
                    String valorAlvo = mNode.has("valorAlvo") ? mNode.get("valorAlvo").asText() : "A definir";
                    String explicacao = mNode.has("explicacao") ? mNode.get("explicacao").asText() : "";
                    metricas.add(new MetricTarget(metrica, valorAlvo, explicacao));
                }
            }
            if (metricas.isEmpty()) {
                metricas.add(new MetricTarget("CAC (Custo de Aquisição)", "< R$ 45,00", "Garantir rentabilidade positiva unit economics"));
                metricas.add(new MetricTarget("Taxa de Conversão Landing Page", "> 3.5%", "Benchmark de mercado para geração de leads"));
                metricas.add(new MetricTarget("ROAS (Retorno sobre Investimento em Anúncios)", "> 4.0x", "Sustentabilidade de escala em tráfego pago"));
            }
            diagnosis.setMetricas(metricas);

            return diagnosis;

        } catch (Exception e) {
            LOGGER.warning(() -> "Falha ao processar JSON da IA, ativando fallback estruturado: " + e.getMessage());
            return createFallbackDiagnosis(rawResponse);
        }
    }

    private String extractJson(String text) {
        Matcher matcher = JSON_BLOCK_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1).trim();
        }
        return text;
    }

    public StructuredDiagnosis createFallbackDiagnosis(String rawText) {
        StructuredDiagnosis diagnosis = new StructuredDiagnosis();
        diagnosis.setDiagnostico(rawText != null && !rawText.isBlank() ? rawText :
                "Diagnóstico Estratégico: Análise consolidada com base no estágio e orçamento da empresa.");

        diagnosis.getOportunidades().add("Refinamento da proposta de valor e eliminação de pontos de fricção no funil.");
        diagnosis.getOportunidades().add("Concentração de orçamento em canais de maior tração comprovada.");

        diagnosis.getAcoesPriorizadas().add(new ActionItem(
                "Otimizar Atendimento e Conversão via WhatsApp",
                "Estruturar roteiro de vendas consultivas para leads quentes",
                "ALTO", "BAIXO", "3 dias"
        ));
        diagnosis.getAcoesPriorizadas().add(new ActionItem(
                "Implementar Google Ads Focado em Fundo de Funil",
                "Capturar buscas de alta intenção de compra",
                "ALTO", "MEDIO", "7 dias"
        ));

        diagnosis.getMetricas().add(new MetricTarget("LTV / CAC", "> 3:1", "Proporção de sustentabilidade econômica"));
        diagnosis.getMetricas().add(new MetricTarget("Taxa de Resposta de Leads", "< 15 minutos", "Impacto direto no fechamento de vendas"));

        return diagnosis;
    }
}
