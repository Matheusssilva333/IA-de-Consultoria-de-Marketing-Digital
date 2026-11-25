package com.mycompany.digitalconsultant.ui;

import com.mycompany.digitalconsultant.ai.FinOpsCostTracker;
import com.mycompany.digitalconsultant.ai.ManusService;
import com.mycompany.digitalconsultant.bpo.BpoProcessingService;
import com.mycompany.digitalconsultant.database.ActionTrackingService;
import com.mycompany.digitalconsultant.database.ConsultationHistoryRepository;
import com.mycompany.digitalconsultant.database.FinancialProfileRepository;
import com.mycompany.digitalconsultant.domain.model.*;
import com.mycompany.digitalconsultant.excel.ExcelBpoIntegrationService;
import com.mycompany.digitalconsultant.report.PdfReportGenerator;
import com.mycompany.digitalconsultant.security.LgpdComplianceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

/**
 * Interface Gráfica Executiva com foco exclusivo no MERCADO FINANCEIRO & BPO.
 * Todas as análises são traduzidas para termos simples e claros de negócios.
 */
public class ModernConsultantApp extends JFrame {

    private final FinancialProfileRepository finProfileRepo = new FinancialProfileRepository();
    private final ConsultationHistoryRepository historyRepo = new ConsultationHistoryRepository();
    private final ActionTrackingService actionService = new ActionTrackingService();
    private final ManusService manusService = new ManusService();
    private final FinOpsCostTracker costTracker = FinOpsCostTracker.getInstance();
    private final LgpdComplianceService lgpdService = LgpdComplianceService.getInstance();
    private final BpoProcessingService bpoService = new BpoProcessingService();
    private final ExcelBpoIntegrationService excelService = new ExcelBpoIntegrationService();
    private final PdfReportGenerator pdfGenerator = new PdfReportGenerator();

    private String currentUser = "sentinel_admin";
    private String currentSessionToken = null;
    private FinancialProfile currentFinancialProfile = null;
    private StructuredDiagnosis lastDiagnosis = null;
    private List<FinancialTransactionItem> currentBpoTransactions = new ArrayList<>();
    private BpoDashboardSummary currentBpoSummary = null;

    // Componentes Visuais
    private JTabbedPane tabbedPane;

    // Aba 1: Perfil Financeiro
    private JTextField txtNomeInstituicao;
    private JComboBox<TipoInstituicaoFinanceira> cbTipoInstituicao;
    private JTextField txtAumOuFaturamento;
    private JComboBox<String> cbRegimeTributario;
    private Map<ModuloBpo, JCheckBox> bpoCheckBoxes = new HashMap<>();
    private Map<ObjetivoMercadoFinanceiro, JCheckBox> objetivoCheckBoxes = new HashMap<>();
    private JCheckBox chkSigiloLgpd;

    // Aba 2: BPO & Excel
    private DefaultTableModel tableModelBpoTransactions;
    private JLabel lblTotalReceitas;
    private JLabel lblTotalDespesas;
    private JLabel lblSaldoLiquido;
    private JLabel lblTaxaConciliacao;

    // Aba 3: Consultoria IA
    private JTextArea txtDesafioFinanceiro;
    private JTextArea txtDiagnosticoView;
    private DefaultListModel<String> listModelOportunidades;
    private DefaultTableModel tableModelAcoes;
    private DefaultTableModel tableModelMetricas;

    // Aba 4: Acompanhamento
    private JComboBox<String> cbHistoricoConsultas;
    private DefaultTableModel tableModelTrackingAcoes;
    private JProgressBar progressImplementacao;
    private List<ConsultationHistoryEntry> historicoCache = new ArrayList<>();

    // Aba 5: FinOps & Auditoria
    private JLabel lblTotalTokens;
    private JLabel lblCustoUSD;
    private JLabel lblCustoBRL;
    private JTextArea txtAuditTrail;

    public ModernConsultantApp() {
        super("Sentinel Financial & BPO Intelligence | SAO System v2.0");
        carregarTransacoesExemploBpo();
        initUI();
    }

    public ModernConsultantApp(String username, String token) {
        this();
        this.currentUser = username;
        this.currentSessionToken = token;
        carregarPerfilFinanceiro();
        carregarHistoricoConsultas();
    }

    private void carregarTransacoesExemploBpo() {
        currentBpoTransactions.clear();
        currentBpoTransactions.add(new FinancialTransactionItem(LocalDate.now(), "RECEITA", "Taxa de Gestão / Administração de Fundos", new BigDecimal("125000.00"), "Cotistas Fundo Alpha", true));
        currentBpoTransactions.add(new FinancialTransactionItem(LocalDate.now(), "RECEITA", "Honorários de BPO Financeiro e Assessoria", new BigDecimal("45000.00"), "Clientes Corporativos", true));
        currentBpoTransactions.add(new FinancialTransactionItem(LocalDate.now(), "DESPESA", "Custódia e Liquidação Financeira (B3 / Selic)", new BigDecimal("18200.00"), "Banco Custodiante", true));
        currentBpoTransactions.add(new FinancialTransactionItem(LocalDate.now(), "DESPESA", "Folha de Pagamento Analistas e Operadores", new BigDecimal("52000.00"), "Equipe Interna", true));
        currentBpoTransactions.add(new FinancialTransactionItem(LocalDate.now(), "TRIBUTO", "ISS e Retenções Tributárias sobre Serviços", new BigDecimal("9800.00"), "Receita Municipal/Federal", false));
        currentBpoTransactions.add(new FinancialTransactionItem(LocalDate.now(), "DESPESA", "Serviços em Nuvem e Terminais de Cotação", new BigDecimal("14500.00"), "Data Provider", false));

        currentBpoSummary = bpoService.processTransactions(currentBpoTransactions);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(15, 23, 42));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(new Color(30, 41, 59));
        tabbedPane.setForeground(new Color(248, 250, 252));

        tabbedPane.addTab("1. Perfil Financeiro & Sigilo (LGPD)", criarPainelPerfilFinanceiro());
        tabbedPane.addTab("2. Módulo BPO & Excel (.xlsx)", criarPainelBpoExcel());
        tabbedPane.addTab("3. Consultor Estratégico IA", criarPainelConsultoriaFinanceira());
        tabbedPane.addTab("4. Acompanhamento de Ações BPO", criarPainelAcompanhamento());
        tabbedPane.addTab("5. FinOps, Sigilo & Auditoria", criarPainelFinOps());

        add(criarHeader(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 41, 59));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("SENTINEL FINANCIAL & BPO INTELLIGENCE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(56, 189, 248));

        JLabel sub = new JLabel("Mercado Financeiro • Gestão de AUM • BPO Financeiro • Integração Excel XLSX • Sigilo Bancário & LGPD");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(148, 163, 184));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(sub);

        header.add(textPanel, BorderLayout.WEST);

        JLabel userBadge = new JLabel("Operador Financeiro: " + currentUser + "  [Sessão Criptografada]");
        userBadge.setForeground(new Color(74, 222, 128));
        userBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.add(userBadge, BorderLayout.EAST);

        return header;
    }

    private JPanel criarStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(new Color(15, 23, 42));
        status.setBorder(new EmptyBorder(6, 15, 6, 15));

        JLabel lbl = new JLabel("Status: Operação Pronta | Criptografia: AES-256-GCM Ativa | Compliance Bacen/CVM/LGPD Garantido");
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.add(lbl, BorderLayout.WEST);

        return status;
    }

    private JPanel criarPainelPerfilFinanceiro() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(15, 23, 42));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(criarLabel("Nome da Instituição / Empresa:"), gbc);
        gbc.gridx = 1;
        txtNomeInstituicao = new JTextField("Alpha Capital Gestão de Recursos", 22);
        form.add(txtNomeInstituicao, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(criarLabel("Tipo de Instituição Financeira:"), gbc);
        gbc.gridx = 1;
        cbTipoInstituicao = new JComboBox<>(TipoInstituicaoFinanceira.values());
        cbTipoInstituicao.setSelectedItem(TipoInstituicaoFinanceira.GESTORA_ASSET);
        form.add(cbTipoInstituicao, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(criarLabel("Patrimônio (AUM) / Faturamento Mensal (R$):"), gbc);
        gbc.gridx = 1;
        txtAumOuFaturamento = new JTextField("45000000.00", 22);
        form.add(txtAumOuFaturamento, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(criarLabel("Regime Tributário:"), gbc);
        gbc.gridx = 1;
        cbRegimeTributario = new JComboBox<>(new String[]{"Lucro Real", "Lucro Presumido", "Simples Nacional"});
        form.add(cbRegimeTributario, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        form.add(criarLabel("Módulos BPO Contratados:"), gbc);
        gbc.gridx = 1;
        JPanel bpoPanel = new JPanel(new GridLayout(3, 2, 4, 4));
        bpoPanel.setOpaque(false);
        for (ModuloBpo mod : ModuloBpo.values()) {
            JCheckBox chk = new JCheckBox(mod.getDescricao());
            chk.setForeground(Color.WHITE);
            chk.setOpaque(false);
            chk.setSelected(true);
            bpoCheckBoxes.put(mod, chk);
            bpoPanel.add(chk);
        }
        form.add(bpoPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        form.add(criarLabel("Objetivos no Mercado Financeiro:"), gbc);
        gbc.gridx = 1;
        JPanel objPanel = new JPanel(new GridLayout(3, 2, 4, 4));
        objPanel.setOpaque(false);
        for (ObjetivoMercadoFinanceiro obj : ObjetivoMercadoFinanceiro.values()) {
            JCheckBox chk = new JCheckBox(obj.getDescricao());
            chk.setForeground(Color.WHITE);
            chk.setOpaque(false);
            if (obj == ObjetivoMercadoFinanceiro.EFICIENCIA_OPERACIONAL_BPO || obj == ObjetivoMercadoFinanceiro.MAXIMIZACAO_LIQUIDEZ) {
                chk.setSelected(true);
            }
            objetivoCheckBoxes.put(obj, chk);
            objPanel.add(chk);
        }
        form.add(objPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        chkSigiloLgpd = new JCheckBox("Declaro conformidade com o Sigilo Bancário (LC 105/01) e LGPD (Lei 13.709/18) para dados cifrados em repouso.");
        chkSigiloLgpd.setSelected(true);
        chkSigiloLgpd.setForeground(new Color(74, 222, 128));
        chkSigiloLgpd.setOpaque(false);
        form.add(chkSigiloLgpd, gbc);

        panel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton btnSalvar = new JButton("Salvar Perfil Financeiro (Criptografia AES-256)");
        btnSalvar.setBackground(new Color(37, 99, 235));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.addActionListener(e -> salvarPerfilFinanceiro());
        btnPanel.add(btnSalvar);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelBpoExcel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(15, 23, 42));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Topo: Indicadores Resumidos de BPO
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        cardsPanel.setOpaque(false);

        lblTotalReceitas = criarCardIndicador("Receitas Realizadas", "R$ 170.000,00", new Color(16, 185, 129), cardsPanel);
        lblTotalDespesas = criarCardIndicador("Despesas Operacionais", "R$ 94.500,00", new Color(239, 68, 68), cardsPanel);
        lblSaldoLiquido = criarCardIndicador("Saldo Líquido em Caixa", "R$ 75.500,00", new Color(56, 189, 248), cardsPanel);
        lblTaxaConciliacao = criarCardIndicador("Conciliação D+1", "66.7%", new Color(245, 158, 11), cardsPanel);

        panel.add(cardsPanel, BorderLayout.NORTH);

        // Centro: Tabela de Transações BPO
        tableModelBpoTransactions = new DefaultTableModel(new String[]{"ID", "Data", "Tipo", "Descrição", "Valor (R$)", "Contraparte", "Conciliado?"}, 0);
        JTable table = new JTable(tableModelBpoTransactions);
        atualizarTabelaBpo();
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Barra Inferior: Integração com Excel
        JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botPanel.setOpaque(false);

        JButton btnImportarExcel = new JButton("Importar Planilha Excel (.xlsx)");
        btnImportarExcel.addActionListener(e -> importarPlanilhaExcel());

        JButton btnExportarExcel = new JButton("Exportar Pasta de Trabalho Excel (.xlsx)");
        btnExportarExcel.setBackground(new Color(22, 163, 74));
        btnExportarExcel.setForeground(Color.WHITE);
        btnExportarExcel.addActionListener(e -> exportarExcelBpo());

        botPanel.add(btnImportarExcel);
        botPanel.add(btnExportarExcel);
        panel.add(botPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel criarCardIndicador(String titulo, String valorInicial, Color corValor, JPanel parent) {
        JPanel card = new JPanel(new GridLayout(2, 1, 2, 2));
        card.setBackground(new Color(30, 41, 59));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lTitulo = new JLabel(titulo);
        lTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lTitulo.setForeground(new Color(148, 163, 184));

        JLabel lValor = new JLabel(valorInicial);
        lValor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lValor.setForeground(corValor);

        card.add(lTitulo);
        card.add(lValor);
        parent.add(card);
        return lValor;
    }

    private JPanel criarPainelConsultoriaFinanceira() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(15, 23, 42));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setOpaque(false);
        top.add(criarLabel("Desafio no Mercado Financeiro / Pergunta do Negócio (Explicada de Forma Simples):"), BorderLayout.NORTH);

        txtDesafioFinanceiro = new JTextArea("Nosso custo de conciliação e fechamento mensal está gerando atraso de 4 dias e R$ 9.800 em pendências tributárias. Como o BPO Financeiro automatizado e a aplicação do caixa no CDI podem aumentar nosso lucro líquido em termos práticos?", 3, 50);
        txtDesafioFinanceiro.setLineWrap(true);
        txtDesafioFinanceiro.setWrapStyleWord(true);
        top.add(new JScrollPane(txtDesafioFinanceiro), BorderLayout.CENTER);

        JButton btnConsultar = new JButton("Executar Consultoria com IA (RAG Financeiro & Tradução Simples)");
        btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConsultar.setBackground(new Color(16, 185, 129));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.addActionListener(e -> executarConsultoriaFinanceira());
        top.add(btnConsultar, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);

        // Seções Estruturadas
        JTabbedPane resultadoTabs = new JTabbedPane();
        resultadoTabs.setBackground(new Color(30, 41, 59));
        resultadoTabs.setForeground(Color.WHITE);

        txtDiagnosticoView = new JTextArea("Execute a consultoria para receber o diagnóstico traduzido em termos práticos de dinheiro em caixa.");
        txtDiagnosticoView.setEditable(false);
        txtDiagnosticoView.setLineWrap(true);
        txtDiagnosticoView.setWrapStyleWord(true);
        txtDiagnosticoView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDiagnosticoView.setBackground(new Color(15, 23, 42));
        txtDiagnosticoView.setForeground(new Color(226, 232, 240));
        resultadoTabs.addTab("Diagnóstico Financeiro & BPO", new JScrollPane(txtDiagnosticoView));

        listModelOportunidades = new DefaultListModel<>();
        JList<String> listOps = new JList<>(listModelOportunidades);
        listOps.setBackground(new Color(15, 23, 42));
        listOps.setForeground(new Color(226, 232, 240));
        resultadoTabs.addTab("Oportunidades de Caixa & Rentabilidade", new JScrollPane(listOps));

        tableModelAcoes = new DefaultTableModel(new String[]{"Impacto em Caixa", "Esforço", "Ação Recomendada", "Descrição Prática", "Prazo"}, 0);
        resultadoTabs.addTab("Plano de Ações Financeiras", new JScrollPane(new JTable(tableModelAcoes)));

        tableModelMetricas = new DefaultTableModel(new String[]{"Indicador / KPI", "Meta / Valor Alvo", "Tradução Simples para o Negócio"}, 0);
        resultadoTabs.addTab("Métricas & Metas Financeiras", new JScrollPane(new JTable(tableModelMetricas)));

        panel.add(resultadoTabs, BorderLayout.CENTER);

        // Botões de Relatório
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportPanel.setOpaque(false);

        JButton btnPdf = new JButton("Gerar Relatório Executivo (PDF)");
        btnPdf.setBackground(new Color(225, 29, 72));
        btnPdf.setForeground(Color.WHITE);
        btnPdf.addActionListener(e -> exportarPdfFinanceiro());

        JButton btnExcel = new JButton("Gerar Pasta de Trabalho Completa (.xlsx)");
        btnExcel.setBackground(new Color(22, 163, 74));
        btnExcel.setForeground(Color.WHITE);
        btnExcel.addActionListener(e -> exportarExcelBpo());

        exportPanel.add(btnPdf);
        exportPanel.add(btnExcel);
        panel.add(exportPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelAcompanhamento() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(15, 23, 42));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(criarLabel("Consultas Anteriores Salvas:"));
        cbHistoricoConsultas = new JComboBox<>();
        cbHistoricoConsultas.setPreferredSize(new Dimension(500, 26));
        cbHistoricoConsultas.addActionListener(e -> selecionarConsultaHistorico());
        top.add(cbHistoricoConsultas);

        panel.add(top, BorderLayout.NORTH);

        tableModelTrackingAcoes = new DefaultTableModel(new Object[]{"Concluída?", "ID", "Impacto em Caixa", "Ação", "Prazo"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        tableModelTrackingAcoes.addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                int row = e.getFirstRow();
                Boolean concluida = (Boolean) tableModelTrackingAcoes.getValueAt(row, 0);
                String acaoId = (String) tableModelTrackingAcoes.getValueAt(row, 1);
                actionService.marcarAcaoConcluida(acaoId, concluida != null && concluida);
                atualizarProgressoVisual();
            }
        });

        panel.add(new JScrollPane(new JTable(tableModelTrackingAcoes)), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setOpaque(false);
        bottom.add(criarLabel("Progresso da Execução das Ações de BPO:"), BorderLayout.NORTH);

        progressImplementacao = new JProgressBar(0, 100);
        progressImplementacao.setStringPainted(true);
        progressImplementacao.setForeground(new Color(16, 185, 129));
        bottom.add(progressImplementacao, BorderLayout.CENTER);

        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel criarPainelFinOps() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBackground(new Color(15, 23, 42));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel metricsPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        metricsPanel.setBackground(new Color(30, 41, 59));
        metricsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(56, 189, 248)),
                "Gestão de Custos FinOps & Telemetria em Dólar e Real",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.WHITE
        ));

        lblTotalTokens = new JLabel("0");
        lblCustoUSD = new JLabel("$0.00 USD");
        lblCustoBRL = new JLabel("R$ 0.00 BRL");

        metricsPanel.add(criarLabel("Tokens Processados na Sessão:"));
        metricsPanel.add(lblTotalTokens);
        metricsPanel.add(criarLabel("Custo Estimado em Dólares:"));
        metricsPanel.add(lblCustoUSD);
        metricsPanel.add(criarLabel("Custo Convertido em Reais:"));
        metricsPanel.add(lblCustoBRL);
        metricsPanel.add(criarLabel("Limite de Proteção Orçamentária:"));
        metricsPanel.add(new JLabel("$5.00 USD (Circuit Breaker)"));

        panel.add(metricsPanel);

        JPanel auditPanel = new JPanel(new BorderLayout(5, 5));
        auditPanel.setBackground(new Color(30, 41, 59));
        auditPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(74, 222, 128)),
                "Governança, Sigilo Bancário & Auditoria LGPD",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.WHITE
        ));

        txtAuditTrail = new JTextArea();
        txtAuditTrail.setEditable(false);
        txtAuditTrail.setBackground(new Color(15, 23, 42));
        txtAuditTrail.setForeground(new Color(148, 163, 184));
        txtAuditTrail.setFont(new Font("Consolas", Font.PLAIN, 10));
        auditPanel.add(new JScrollPane(txtAuditTrail), BorderLayout.CENTER);

        panel.add(auditPanel);
        return panel;
    }

    private JLabel criarLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(226, 232, 240));
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return l;
    }

    // ==========================================
    // LÓGICA DE NEGÓCIO & INTEGRAÇÃO FINANCEIRA
    // ==========================================

    private void salvarPerfilFinanceiro() {
        try {
            FinancialProfile p = new FinancialProfile();
            p.setNomeInstituicao(txtNomeInstituicao.getText().trim());
            p.setTipo((TipoInstituicaoFinanceira) cbTipoInstituicao.getSelectedItem());
            p.setAumOuFaturamentoMensal(new BigDecimal(txtAumOuFaturamento.getText().trim()));
            p.setRegimeTributario((String) cbRegimeTributario.getSelectedItem());
            p.setResponsavelFinanceiro(currentUser);
            p.setConsentimentoLgpdSigilo(chkSigiloLgpd.isSelected());

            for (Map.Entry<ModuloBpo, JCheckBox> e : bpoCheckBoxes.entrySet()) {
                if (e.getValue().isSelected()) p.addModuloBpo(e.getKey());
            }
            for (Map.Entry<ObjetivoMercadoFinanceiro, JCheckBox> e : objetivoCheckBoxes.entrySet()) {
                if (e.getValue().isSelected()) p.addObjetivo(e.getKey());
            }

            finProfileRepo.salvarOuAtualizar(p);
            this.currentFinancialProfile = p;
            JOptionPane.showMessageDialog(this, "Perfil financeiro salvo com Criptografia AES-256 e Sigilo Bancário!", "Segurança Garantida", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar perfil: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarPerfilFinanceiro() {
        finProfileRepo.buscarPorUsuario(currentUser).ifPresent(p -> {
            this.currentFinancialProfile = p;
            txtNomeInstituicao.setText(p.getNomeInstituicao());
            cbTipoInstituicao.setSelectedItem(p.getTipo());
            txtAumOuFaturamento.setText(p.getAumOuFaturamentoMensal().toString());
            cbRegimeTributario.setSelectedItem(p.getRegimeTributario());
            chkSigiloLgpd.setSelected(p.isConsentimentoLgpdSigilo());
        });
    }

    private void atualizarTabelaBpo() {
        tableModelBpoTransactions.setRowCount(0);
        for (FinancialTransactionItem item : currentBpoTransactions) {
            tableModelBpoTransactions.addRow(new Object[]{
                    item.getId(), item.getDataCompetencia(), item.getTipo(), item.getDescricao(),
                    String.format("R$ %.2f", item.getValor()), item.getContraparte(),
                    item.isConciliado() ? "SIM (Conciliado)" : "PENDENTE"
            });
        }
        if (currentBpoSummary != null) {
            lblTotalReceitas.setText(String.format("R$ %.2f", currentBpoSummary.totalReceitas()));
            lblTotalDespesas.setText(String.format("R$ %.2f", currentBpoSummary.totalDespesas()));
            lblSaldoLiquido.setText(String.format("R$ %.2f", currentBpoSummary.saldoLiquido()));
            lblTaxaConciliacao.setText(String.format("%.1f%%", currentBpoSummary.taxaConciliacaoPercent()));
        }
    }

    private void importarPlanilhaExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecione a Planilha Excel (.xlsx) de Lançamentos Financeiros");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                List<FinancialTransactionItem> imported = excelService.importFinancialWorkbook(f);
                if (!imported.isEmpty()) {
                    currentBpoTransactions.addAll(imported);
                    currentBpoSummary = bpoService.processTransactions(currentBpoTransactions);
                    atualizarTabelaBpo();
                    JOptionPane.showMessageDialog(this, imported.size() + " lançamentos importados com sucesso da planilha Excel!", "Excel Importado", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao ler Excel: " + e.getMessage(), "Falha", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarExcelBpo() {
        try {
            String path = "./relatorios/Relatorio_Executivo_BPO_" + System.currentTimeMillis() + ".xlsx";
            File f = excelService.exportFinancialWorkbook(currentFinancialProfile, lastDiagnosis, currentBpoTransactions, path);
            JOptionPane.showMessageDialog(this, "Pasta de trabalho Excel (.xlsx) gerada com sucesso em:\n" + f.getAbsolutePath(), "Excel Gerado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar Excel: " + e.getMessage(), "Falha", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executarConsultoriaFinanceira() {
        if (!chkSigiloLgpd.isSelected()) {
            JOptionPane.showMessageDialog(this, "O consentimento formal de sigilo bancário é mandatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String desafio = txtDesafioFinanceiro.getText().trim();
        if (desafio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Descreva o desafio financeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentFinancialProfile == null) {
            salvarPerfilFinanceiro();
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            StructuredDiagnosis diag = manusService.performFinancialConsultation(currentFinancialProfile, desafio, currentBpoSummary);
            this.lastDiagnosis = diag;

            // Salva no Histórico
            ConsultationHistoryEntry entry = new ConsultationHistoryEntry(
                    UUID.randomUUID().toString(), currentUser, currentFinancialProfile.getId(), desafio, diag, LocalDateTime.now()
            );
            historyRepo.salvarConsulta(entry);

            // Atualiza Telas
            txtDiagnosticoView.setText(diag.getDiagnostico());

            listModelOportunidades.clear();
            for (String op : diag.getOportunidades()) {
                listModelOportunidades.addElement("• " + op);
            }

            tableModelAcoes.setRowCount(0);
            for (ActionItem a : diag.getAcoesPriorizadas()) {
                tableModelAcoes.addRow(new Object[]{a.getImpacto(), a.getEsforco(), a.getTitulo(), a.getDescricao(), a.getPrazoSugerido()});
            }

            tableModelMetricas.setRowCount(0);
            for (MetricTarget m : diag.getMetricas()) {
                tableModelMetricas.addRow(new Object[]{m.getMetrica(), m.getValorAlvo(), m.getExplicacao()});
            }

            atualizarFinOpsView();
            carregarHistoricoConsultas();
            tabbedPane.setSelectedIndex(2);

            JOptionPane.showMessageDialog(this, "Consultoria Financeira executada com tradução simples para os negócios!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Falha na consultoria: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void carregarHistoricoConsultas() {
        cbHistoricoConsultas.removeAllItems();
        historicoCache = historyRepo.listarPorUsuario(currentUser);
        for (ConsultationHistoryEntry h : historicoCache) {
            String label = String.format("%s - %s",
                    h.getDataHora() != null ? h.getDataHora().toString().substring(0, 16).replace("T", " ") : "",
                    h.getPerguntaOriginal().length() > 50 ? h.getPerguntaOriginal().substring(0, 50) + "..." : h.getPerguntaOriginal()
            );
            cbHistoricoConsultas.addItem(label);
        }
        if (!historicoCache.isEmpty()) {
            cbHistoricoConsultas.setSelectedIndex(0);
            selecionarConsultaHistorico();
        }
    }

    private void selecionarConsultaHistorico() {
        int idx = cbHistoricoConsultas.getSelectedIndex();
        if (idx >= 0 && idx < historicoCache.size()) {
            ConsultationHistoryEntry entry = historicoCache.get(idx);
            List<ActionItem> acoes = actionService.listarAcoesPorConsulta(entry.getId());

            tableModelTrackingAcoes.setRowCount(0);
            for (ActionItem a : acoes) {
                tableModelTrackingAcoes.addRow(new Object[]{a.isConcluida(), a.getId(), a.getImpacto(), a.getTitulo(), a.getPrazoSugerido()});
            }
            atualizarProgressoVisual();
        }
    }

    private void atualizarProgressoVisual() {
        int idx = cbHistoricoConsultas.getSelectedIndex();
        if (idx >= 0 && idx < historicoCache.size()) {
            double progresso = actionService.calcularProgressoImplementacao(historicoCache.get(idx).getId());
            progressImplementacao.setValue((int) progresso);
            progressImplementacao.setString(String.format("%.1f%% Implementado", progresso));
        }
    }

    private void atualizarFinOpsView() {
        int tokens = costTracker.getTotalTokensAccumulated();
        double usd = costTracker.getTotalCostAccumulatedUSD();
        double brl = costTracker.calculateCostBRL(usd);

        lblTotalTokens.setText(String.valueOf(tokens));
        lblCustoUSD.setText(String.format("$%.4f USD", usd));
        lblCustoBRL.setText(String.format("R$ %.2f BRL", brl));

        StringBuilder sb = new StringBuilder();
        for (LgpdComplianceService.AuditLogEntry log : lgpdService.getAuditTrail()) {
            sb.append(String.format("[%s] OP: %s | USER: %s | %s\n",
                    log.timestamp().toString().substring(11, 19), log.operacao(), log.usuario(), log.detalhe()));
        }
        txtAuditTrail.setText(sb.toString());
    }

    private void exportarPdfFinanceiro() {
        if (lastDiagnosis == null) {
            JOptionPane.showMessageDialog(this, "Execute a consultoria antes de exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String path = "./relatorios/Relatorio_Financeiro_" + System.currentTimeMillis() + ".pdf";
            CompanyProfile comp = new CompanyProfile();
            comp.setNomeEmpresa(currentFinancialProfile != null ? currentFinancialProfile.getNomeInstituicao() : "Instituição Financeira");
            comp.setSetorAtuacao(currentFinancialProfile != null ? currentFinancialProfile.getTipo().getDescricao() : "Mercado Financeiro");
            comp.setOrcamentoMensal(currentFinancialProfile != null ? currentFinancialProfile.getAumOuFaturamentoMensal() : BigDecimal.ZERO);
            comp.setPorte(PorteEmpresa.GRANDE);

            File f = pdfGenerator.generatePdfReport(comp, lastDiagnosis, path);
            JOptionPane.showMessageDialog(this, "Relatório executivo em PDF gerado em:\n" + f.getAbsolutePath(), "PDF Criado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar PDF: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new ModernConsultantApp().setVisible(true);
        });
    }
}
