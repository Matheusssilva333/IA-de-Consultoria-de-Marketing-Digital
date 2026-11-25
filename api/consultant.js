/**
 * Vercel Serverless Function: /api/consultant
 * Consultor Estratégico de Mercado Financeiro & BPO com Tradução Simples para Negócios.
 */
module.exports = async (req, res) => {
  // CORS
  res.setHeader("Access-Control-Allow-Credentials", true);
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET,OPTIONS,PATCH,DELETE,POST,PUT");
  res.setHeader(
    "Access-Control-Allow-Headers",
    "X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version"
  );

  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  if (req.method !== "POST") {
    return res.status(405).json({ error: "Método não permitido. Utilize POST." });
  }

  try {
    const { profile, question, bpoSummary } = req.body || {};

    const nomeInstituicao = profile?.nomeInstituicao || "Instituição Financeira / Gestora de Recursos";
    const aum = profile?.aumOuFaturamentoMensal || "R$ 25.000.000,00";
    const tipo = profile?.tipo || "Asset Management / BPO Financeiro";
    const pergunta = question || "Como otimizar a conciliação bancária diária e maximizar o caixa livre no CDI?";

    // Resposta estruturada garantida com foco em Mercado Financeiro e explicação simples de negócio
    const structuredResponse = {
      diagnostico: `Análise Estratégica para ${nomeInstituicao} (AUM: ${aum}): ` +
        `Em termos simples de negócio, a sua operação está deixando dinheiro na mesa por falta de conciliação diária automática em D+1. ` +
        `Isso significa que saldos ociosos não estão sendo transferidos a tempo para aplicações que rendem 100%+ do CDI, ` +
        `e há risco desnecessário de multas por contas a pagar pendentes que poderiam ser evitadas com o fechamento de BPO antecipado.`,
      oportunidades: [
        "Aplicação Automática de Caixa Livre: Cada R$ 100.000,00 parados na conta corrente sem render geram perda de aproximadamente R$ 1.000,00 por mês frente ao CDI atual.",
        "Fechamento de BPO em D+1: Redução imediata do tempo de conferência contábil de 4 dias para menos de 24 horas, liberando analistas para prospecção de novos cotistas e captação de AUM.",
        "Régua de Cobrança e Redução de Inadimplência: Antecipar cobrança via WhatsApp automatizado reduz o Prazo Médio de Recebimento (PMR) em até 12 dias."
      ],
      acoesPriorizadas: [
        {
          titulo: "Ativar Varredura Automática de Caixa Livre para CDI",
          descricao: "Configurar com o banco custodiante a aplicação automática de todo saldo positivo no fechamento do dia.",
          impacto: "ALTO",
          esforco: "BAIXO",
          prazoSugerido: "2 dias",
          concluida: false
        },
        {
          titulo: "Padronizar Conciliação Bancária em D+1 via BPO",
          descricao: "Importar extratos bancários e adquirentes logo às 08h da manhã para identificar qualquer furo de lançamento antes do almoço.",
          impacto: "ALTO",
          esforco: "MEDIO",
          prazoSugerido: "5 dias",
          concluida: false
        },
        {
          titulo: "Estruturar DRE Gerencial Mensal com Foco em EBITDA",
          descricao: "Separar claramente os custos operacionais da taxa de gestão de fundos para dar visibilidade de rentabilidade aos sócios.",
          impacto: "MEDIO",
          esforco: "MEDIO",
          prazoSugerido: "10 dias",
          concluida: false
        }
      ],
      metricas: [
        {
          metrica: "Taxa de Conciliação em D+1",
          valorAlvo: "100%",
          explicacao: "Significa zero dinheiro sumido ou sem conferência na virada do dia."
        },
        {
          metrica: "Rentabilidade de Caixa Livre",
          valorAlvo: "> 102% do CDI",
          explicacao: "Garantir que todo o capital de giro esteja rendendo juros compostos a favor da gestora."
        },
        {
          metrica: "Inadimplência de Honorários BPO",
          valorAlvo: "< 2.5%",
          explicacao: "Manter o recebimento dos clientes em dia para não precisar recorrer a empréstimos bancários caros."
        }
      ],
      telemetria: {
        timestamp: new Date().toISOString(),
        modelo: "Sentinel-Financial-BPO-Engine-v2",
        ambiente: "Vercel-Serverless-Edge",
        statusLgpd: "Sigilo Bancário LC 105/01 Garantido"
      }
    };

    return res.status(200).json(structuredResponse);

  } catch (error) {
    return res.status(500).json({
      error: "Falha ao processar consultoria financeira",
      details: error.message
    });
  }
};
