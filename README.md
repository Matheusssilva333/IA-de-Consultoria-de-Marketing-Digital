# SENTINEL FINANCIAL & BPO INTELLIGENCE | SAO System v2.0
> **Assistente Estratégico Soberano para o Mercado Financeiro, Gestão de AUM, BPO Financeiro e Integração Nativa com Excel (.xlsx)**

---

## 🏛️ 1. Foco Estratégico: Mercado Financeiro & BPO
O sistema foi integralmente adaptado para ter como **foco único e principal o Mercado Financeiro e operações de BPO**:
- **Público-Alvo**: Gestoras de Recursos (Asset Management), Assessorias de Investimento (AIs credenciados), Family Offices (Wealth Management), Fintechs de Crédito e Empresas atendidas por BPO Financeiro.
- **Módulos Operacionais de BPO**:
  - Contas a Pagar e Receber
  - Conciliação Bancária em D+1 (Extratos vs. Livros Contábeis)
  - Projeção de Fluxo de Caixa Direto e Runway (Capital de Giro)
  - DRE Gerencial & Cálculo de EBITDA
  - Mitigação de Inadimplência e Régua de Cobrança
- **Diretriz de Voz Suprema**: O assistente **SEMPRE explica tudo de forma simples e clara, SEMPRE traduzindo conceitos técnicos para os negócios do mercado financeiro** (ex: traduzindo "timeout e latência" para "prevenção de slippage em liquidação", e "otimização de fluxo" para "maximização do caixa livre aplicado a 100%+ do CDI").

---

## 📊 2. Integração Nativa com Excel (.xlsx via Apache POI)
- **Leitura & Ingestão de Planilhas**: Parsing automático de extratos, razões e lançamentos bancários com validação de tipos numéricos e de data.
- **Exportação de Pastas de Trabalho Executivas (.xlsx)**:
  - *Aba 1: DRE & Diagnóstico Financeiro*
  - *Aba 2: Transações & Conciliação BPO*
  - *Aba 3: Plano de Ações Financeiras*
  - *Aba 4: KPIs & Métricas de Mercado Financeiro*
  - Formatação com estilos corporativos, moeda brasileira (`R$ #,##0.00`) e autoajuste de colunas.

---

## 🔒 3. Blindagem de Dados, Sigilo Bancário & LGPD
- **Criptografia em Repouso AES-256-GCM**: O Patrimônio sob Gestão (AUM) e faturamentos são cifrados em repouso com Salt PBKDF2 e IV aleatório por registro. Prova automatizada em teste JDBC comprova ausência de texto puro no disco.
- **Autenticação Segura (OWASP)**: Hashing com `PBKDF2WithHmacSHA256` (100.000 iterações), Salt criptográfico individual e bloqueio automático de conta após 5 tentativas inválidas no `SessionManager`.
- **Sigilo Bancário (LC 105/01) & LGPD (Lei 13.709/18)**: Mascaramento de dados bancários/PII e atendimento ao Direito ao Esquecimento (Art. 18).

---

## 🌐 4. Data Mesh Financeiro Atualizado
- `FinancialProfileProduct` (Domínio de Perfil da Instituição): Classificado sob a Lei de Sigilo Bancário e LGPD.
- `BpoOperationsProduct` (Domínio Operacional BPO): Validação de integridade contábil matemática (`Saldo Líquido = Receitas - Despesas`).
- `FinancialIntelligenceProduct` (Domínio de Diagnóstico e Estratégia): Diagnósticos com foco em AUM e liquidez.
- `DataMeshFederationRegistry`: Validação e federação contínua de contratos em tempo de execução.

---

## 🧪 5. Evidência Irrefutável de Testes (QA & Auditoria)
A suíte completa foi validada com **JUnit 5**:

```powershell
.\mvnw.ps1 test
```

### Resultados da Execução:
- `RagAndFinOpsEngineTest`: 3/3 aprovados.
- `ConsultantEndToEndFlowTest`: 1/1 aprovado.
- `DatabaseAndHistoryTrackingTest`: 3/3 aprovados.
- `DataMeshAndGovernanceTest`: 3/3 aprovados.
- `ExcelAndBpoIntegrationTest`: 2/2 aprovados (Geração e Leitura XLSX POI).
- `FinancialConsultantEndToEndTest`: 1/1 aprovado (Ciclo E2E completo: Login → Sigilo → BPO → RAG Financeiro → IA com Tradução Simples → Data Mesh → Excel XLSX).
- `FinancialDataSecurityTest`: 2/2 aprovados (Criptografia JDBC e Mascaramento Bancário).
- `SecurityAndLgpdTest`: 6/6 aprovados (PBKDF2, Brute-Force, AES-GCM, LGPD).

**Total**: `21/21 Testes Aprovados - 0 Falhas - 0 Erros - BUILD SUCCESS`.

---

## 🚀 6. Modos de Execução

**1. Interface Gráfica Executiva (Swing Dark Mode):**
```powershell
.\mvnw.ps1 compile exec:java
```
*Credenciais padrão: `sentinel_admin` / `Admin@2026#Secure` (ou cadastre novas na tela de autenticação).*

**2. Modo Terminal Headless (CLI):**
```powershell
.\mvnw.ps1 exec:java -Dexec.args="--cli"
```
