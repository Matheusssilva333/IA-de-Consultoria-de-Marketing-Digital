package Interfaceweb;

import com.mycompany.digitalconsultant.database.UserRepository;
import com.mycompany.digitalconsultant.domain.model.UserAccount;
import com.mycompany.digitalconsultant.security.PasswordSecurity;
import com.mycompany.digitalconsultant.security.SessionManager;
import com.mycompany.digitalconsultant.ui.ModernConsultantApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Interface de Autenticação Segura (Login & Cadastro) em conformidade com OWASP e LGPD.
 */
public class Login extends javax.swing.JFrame {

    private final UserRepository userRepo = new UserRepository();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private JTextField txtNome;
    private JPasswordField txtSenha;
    private JTextField txtEmail;
    private JCheckBox chkModoCadastro;
    private JLabel lblFeedback;
    private JButton btnEntrar;

    public Login() {
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("Sentinel Digital Consultant - Autenticação Segura");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(15, 23, 42));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(15, 23, 42));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Cabeçalho
        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setOpaque(false);
        JLabel title = new JLabel("ACESSO AO SISTEMA", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(56, 189, 248));

        JLabel subtitle = new JLabel("Autenticação com Salt Criptográfico PBKDF2 & LGPD", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(new Color(148, 163, 184));

        header.add(title);
        header.add(subtitle);
        mainPanel.add(header, BorderLayout.NORTH);

        // Formulário
        JPanel form = new JPanel(new GridLayout(8, 1, 4, 4));
        form.setOpaque(false);

        JLabel lblNome = new JLabel("Usuário:");
        lblNome.setForeground(Color.WHITE);
        txtNome = new JTextField("sentinel_admin");

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setForeground(Color.WHITE);
        txtSenha = new JPasswordField("Admin@2026#Secure");

        JLabel lblEmail = new JLabel("E-mail corporativo (para cadastro):");
        lblEmail.setForeground(Color.WHITE);
        txtEmail = new JTextField("contato@empresa.com.br");
        txtEmail.setEnabled(false);

        chkModoCadastro = new JCheckBox("Criar nova conta de usuário");
        chkModoCadastro.setOpaque(false);
        chkModoCadastro.setForeground(new Color(148, 163, 184));
        chkModoCadastro.addActionListener(e -> {
            boolean cadastro = chkModoCadastro.isSelected();
            txtEmail.setEnabled(cadastro);
            btnEntrar.setText(cadastro ? "Cadastrar e Acessar" : "Entrar no Sistema");
        });

        lblFeedback = new JLabel(" ", SwingConstants.CENTER);
        lblFeedback.setForeground(new Color(239, 68, 68));
        lblFeedback.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        form.add(lblNome);
        form.add(txtNome);
        form.add(lblSenha);
        form.add(txtSenha);
        form.add(lblEmail);
        form.add(txtEmail);
        form.add(chkModoCadastro);
        form.add(lblFeedback);

        mainPanel.add(form, BorderLayout.CENTER);

        // Botão Entrar
        btnEntrar = new JButton("Entrar no Sistema");
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEntrar.setBackground(new Color(37, 99, 235));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setPreferredSize(new Dimension(200, 38));
        btnEntrar.addActionListener(e -> autenticar());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(btnEntrar);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Garante a existência do usuário padrão de demonstração se o banco estiver limpo
        garantirUsuarioPadrao();
    }

    private void garantirUsuarioPadrao() {
        if (userRepo.buscarPorUsername("sentinel_admin").isEmpty()) {
            String salt = PasswordSecurity.generateSalt();
            String hash = PasswordSecurity.hashPassword("Admin@2026#Secure", salt);
            userRepo.salvar(new UserAccount("sentinel_admin", hash, salt, "Administrador Sentinela", "admin@sentinela.ai"));
        }
    }

    private void autenticar() {
        String username = txtNome.getText().trim();
        String password = new String(txtSenha.getPassword());
        boolean modoCadastro = chkModoCadastro.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            lblFeedback.setText("Preencha usuário e senha.");
            return;
        }

        if (sessionManager.isUserLockedOut(username)) {
            lblFeedback.setText("Usuário bloqueado temporariamente por excesso de tentativas.");
            return;
        }

        if (modoCadastro) {
            if (!PasswordSecurity.isPasswordStrong(password)) {
                lblFeedback.setText("Senha fraca! Use min. 8 chars, maiúsculas, minúsculas e números.");
                return;
            }
            if (userRepo.buscarPorUsername(username).isPresent()) {
                lblFeedback.setText("Nome de usuário já existente.");
                return;
            }
            String salt = PasswordSecurity.generateSalt();
            String hash = PasswordSecurity.hashPassword(password, salt);
            String email = txtEmail.getText().trim();
            userRepo.salvar(new UserAccount(username, hash, salt, username, email));
        }

        // Validação de Credenciais
        var userOpt = userRepo.buscarPorUsername(username);
        if (userOpt.isPresent()) {
            UserAccount user = userOpt.get();
            if (PasswordSecurity.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
                sessionManager.recordSuccessfulLogin(username);
                userRepo.atualizarUltimoLogin(username);
                String token = sessionManager.createSession(username);

                // Acesso autorizado -> Abre ModernConsultantApp
                SwingUtilities.invokeLater(() -> {
                    new ModernConsultantApp(username, token).setVisible(true);
                    dispose();
                });
                return;
            }
        }

        sessionManager.recordFailedAttempt(username);
        lblFeedback.setText("Credenciais inválidas. Tentativa registrada.");
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }
}
