package com.mycompany.digitalconsultant.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Gestor do Banco de Dados Embarcado H2 com suporte a Criptografia em Repouso (LGPD & Sigilo Bancário).
 */
public class DatabaseManager {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private static final String DB_DIR = "./data";
    private static final String DB_FILE = DB_DIR + "/consultant_secure_db";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sao_sentinel_db_pwd_2026";

    private String jdbcUrl = "jdbc:h2:file:" + DB_FILE + ";DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";

    private DatabaseManager() {
        initDatabase();
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public void configureInMemoryMode(String testDbName) {
        this.jdbcUrl = "jdbc:h2:mem:" + testDbName + ";DB_CLOSE_DELAY=-1";
        initDatabase();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, DB_USER, DB_PASSWORD);
    }

    private void initDatabase() {
        try {
            File dir = new File(DB_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                // Tabela de Usuários
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS tb_usuarios (
                        username VARCHAR(100) PRIMARY KEY,
                        password_hash VARCHAR(255) NOT NULL,
                        salt VARCHAR(100) NOT NULL,
                        nome_completo VARCHAR(200),
                        email VARCHAR(200),
                        criado_em VARCHAR(50),
                        ultimo_login VARCHAR(50)
                    );
                """);

                // Tabela de Perfis de Empresas (Marketing)
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS tb_empresas (
                        id VARCHAR(100) PRIMARY KEY,
                        nome_empresa VARCHAR(255) NOT NULL,
                        setor VARCHAR(150),
                        porte VARCHAR(50),
                        canais_json CLOB,
                        orcamento_cifrado VARCHAR(255),
                        metas_json CLOB,
                        consentimento_lgpd BOOLEAN,
                        data_consentimento VARCHAR(50),
                        usuario_responsavel VARCHAR(100)
                    );
                """);

                // Tabela de Perfis Financeiros & BPO (Criptografia AES-256 e Sigilo Bancário)
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS tb_perfis_financeiros (
                        id VARCHAR(100) PRIMARY KEY,
                        nome_instituicao VARCHAR(255) NOT NULL,
                        tipo VARCHAR(100),
                        aum_cifrado VARCHAR(255),
                        regime_tributario VARCHAR(100),
                        modulos_bpo_json CLOB,
                        objetivos_json CLOB,
                        consentimento_sigilo BOOLEAN,
                        data_consentimento VARCHAR(50),
                        responsavel_financeiro VARCHAR(100)
                    );
                """);

                // Tabela de Consultas e Diagnósticos Estruturados
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS tb_consultas (
                        id VARCHAR(100) PRIMARY KEY,
                        usuario VARCHAR(100) NOT NULL,
                        empresa_id VARCHAR(100),
                        pergunta CLOB,
                        resultado_json CLOB,
                        data_hora VARCHAR(50)
                    );
                """);

                // Tabela de Ações e Acompanhamento de Execução (Checklist)
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS tb_acoes (
                        id VARCHAR(100) PRIMARY KEY,
                        consulta_id VARCHAR(100) NOT NULL,
                        titulo VARCHAR(255) NOT NULL,
                        descricao CLOB,
                        impacto VARCHAR(50),
                        esforco VARCHAR(50),
                        prazo VARCHAR(100),
                        concluida BOOLEAN DEFAULT FALSE,
                        FOREIGN KEY (consulta_id) REFERENCES tb_consultas(id) ON DELETE CASCADE
                    );
                """);

                LOGGER.info("[Database] Estrutura do banco de dados (inclusive financeiro) inicializada com sucesso.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao inicializar banco de dados: " + e.getMessage(), e);
        }
    }
}
