package com.mycompany.digitalconsultant.database;

import com.mycompany.digitalconsultant.domain.model.ActionItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de Acompanhamento de Ações Concluídas (Etapa 5 do fluxograma).
 * Permite que o cliente acompanhe a execução prática das recomendações estratégicas.
 */
public class ActionTrackingService {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public void marcarAcaoConcluida(String acaoId, boolean concluida) {
        String sql = "UPDATE tb_acoes SET concluida = ? WHERE id = ?;";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, concluida);
            ps.setString(2, acaoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da ação: " + e.getMessage(), e);
        }
    }

    public List<ActionItem> listarAcoesPorConsulta(String consultaId) {
        List<ActionItem> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_acoes WHERE consulta_id = ? ORDER BY id ASC;";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, consultaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ActionItem(
                            rs.getString("id"),
                            rs.getString("titulo"),
                            rs.getString("descricao"),
                            rs.getString("impacto"),
                            rs.getString("esforco"),
                            rs.getString("prazo"),
                            rs.getBoolean("concluida")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ações da consulta: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Retorna o percentual de implementação das ações (0 a 100%).
     */
    public double calcularProgressoImplementacao(String consultaId) {
        List<ActionItem> acoes = listarAcoesPorConsulta(consultaId);
        if (acoes.isEmpty()) return 0.0;
        long concluidas = acoes.stream().filter(ActionItem::isConcluida).count();
        return (concluidas * 100.0) / acoes.size();
    }
}
