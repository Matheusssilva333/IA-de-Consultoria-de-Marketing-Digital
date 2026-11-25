package com.mycompany.digitalconsultant.database;

import com.mycompany.digitalconsultant.domain.model.UserAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositório de Usuários com autenticação segura.
 */
public class UserRepository {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public void salvar(UserAccount user) {
        String sql = """
            MERGE INTO tb_usuarios (username, password_hash, salt, nome_completo, email, criado_em, ultimo_login)
            KEY (username) VALUES (?, ?, ?, ?, ?, ?, ?);
        """;
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername().toLowerCase());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getSalt());
            ps.setString(4, user.getNomeCompleto());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getCriadoEm() != null ? user.getCriadoEm().toString() : LocalDateTime.now().toString());
            ps.setString(7, user.getUltimoLogin() != null ? user.getUltimoLogin().toString() : LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar conta de usuário: " + e.getMessage(), e);
        }
    }

    public Optional<UserAccount> buscarPorUsername(String username) {
        if (username == null) return Optional.empty();
        String sql = "SELECT * FROM tb_usuarios WHERE LOWER(username) = LOWER(?);";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserAccount user = new UserAccount();
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setSalt(rs.getString("salt"));
                    user.setNomeCompleto(rs.getString("nome_completo"));
                    user.setEmail(rs.getString("email"));
                    String criadoEm = rs.getString("criado_em");
                    if (criadoEm != null) user.setCriadoEm(LocalDateTime.parse(criadoEm));
                    String ultimoLogin = rs.getString("ultimo_login");
                    if (ultimoLogin != null) user.setUltimoLogin(LocalDateTime.parse(ultimoLogin));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void atualizarUltimoLogin(String username) {
        String sql = "UPDATE tb_usuarios SET ultimo_login = ? WHERE LOWER(username) = LOWER(?);";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, LocalDateTime.now().toString());
            ps.setString(2, username.trim());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar timestamp de login: " + e.getMessage());
        }
    }
}
