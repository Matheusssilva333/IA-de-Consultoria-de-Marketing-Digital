package com.mycompany.digitalconsultant.ai;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Gestor FinOps de Custos e Consumo de Tokens (Etapa 6 do fluxograma).
 * Controla teto de orçamento, estimativa de tokens e monitoramento de custos.
 */
public class FinOpsCostTracker {

    private static final Logger LOGGER = Logger.getLogger(FinOpsCostTracker.class.getName());
    private static final FinOpsCostTracker INSTANCE = new FinOpsCostTracker();

    // Valores de referência por 1.000 tokens (estimativa para modelos de mercado em USD)
    private static final double PRICE_PROMPT_PER_1K_TOKENS = 0.0015;
    private static final double PRICE_COMPLETION_PER_1K_TOKENS = 0.0020;
    private static final double USD_TO_BRL_RATE = 5.60;

    // Teto de gastos por sessão (padrão $5.00 USD)
    private final AtomicReference<Double> costLimitUSD = new AtomicReference<>(5.00);

    private final AtomicInteger totalPromptTokensAccumulated = new AtomicInteger(0);
    private final AtomicInteger totalCompletionTokensAccumulated = new AtomicInteger(0);
    private final AtomicReference<Double> totalCostAccumulatedUSD = new AtomicReference<>(0.0);

    private FinOpsCostTracker() {}

    public static FinOpsCostTracker getInstance() {
        return INSTANCE;
    }

    /**
     * Estimador de tokens baseado na contagem de palavras e caracteres (média 1 token ~= 4 chars em pt-BR).
     */
    public int estimateTokens(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        int charCount = text.length();
        int words = text.split("\\s+").length;
        // Aproximação estatística refinada para português técnico
        return Math.max(words, (int) Math.ceil(charCount / 3.8));
    }

    public synchronized double calculateCostUSD(int promptTokens, int completionTokens) {
        double promptCost = (promptTokens / 1000.0) * PRICE_PROMPT_PER_1K_TOKENS;
        double completionCost = (completionTokens / 1000.0) * PRICE_COMPLETION_PER_1K_TOKENS;
        return BigDecimal.valueOf(promptCost + completionCost)
                .setScale(6, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public synchronized double calculateCostBRL(double costUSD) {
        return BigDecimal.valueOf(costUSD * USD_TO_BRL_RATE)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public synchronized boolean isCostLimitExceeded(double additionalCostUSD) {
        return (totalCostAccumulatedUSD.get() + additionalCostUSD) > costLimitUSD.get();
    }

    public synchronized void recordUsage(int promptTokens, int completionTokens) {
        double cost = calculateCostUSD(promptTokens, completionTokens);
        totalPromptTokensAccumulated.addAndGet(promptTokens);
        totalCompletionTokensAccumulated.addAndGet(completionTokens);
        totalCostAccumulatedUSD.updateAndGet(current -> current + cost);

        LOGGER.info(() -> String.format(
                "[FinOps Tracking] +%d tokens | Custo req: $%.5f USD | Acumulado: $%.4f USD (R$ %.2f BRL)",
                (promptTokens + completionTokens), cost, totalCostAccumulatedUSD.get(),
                calculateCostBRL(totalCostAccumulatedUSD.get())
        ));
    }

    public void setCostLimitUSD(double limitUSD) {
        this.costLimitUSD.set(limitUSD);
    }

    public double getCostLimitUSD() {
        return costLimitUSD.get();
    }

    public double getTotalCostAccumulatedUSD() {
        return totalCostAccumulatedUSD.get();
    }

    public int getTotalTokensAccumulated() {
        return totalPromptTokensAccumulated.get() + totalCompletionTokensAccumulated.get();
    }

    public void resetSessionMetrics() {
        totalPromptTokensAccumulated.set(0);
        totalCompletionTokensAccumulated.set(0);
        totalCostAccumulatedUSD.set(0.0);
    }
}
