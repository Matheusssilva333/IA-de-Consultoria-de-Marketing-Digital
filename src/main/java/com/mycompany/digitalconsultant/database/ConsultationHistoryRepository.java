package com.mycompany.digitalconsultant.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.digitalconsultant.domain.model.ActionItem;
import com.mycompany.digitalconsultant.domain.model.ConsultationHistoryEntry;
import com.mycompany.digitalconsultant.domain.model.StructuredDiagnosis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório de Histórico e Acompanhamento de Consultorias (Etapa 5).
 */
public class ConsultationHistoryRepository {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public void salvarConsulta(ConsultationHistoryEntry entry) {
        if (entry.getId() == null || entry.getId().isBlank()) {
            entry.setId(UUID.randomUUID().toString());
        }

        String sqlConsulta = """
            INSERT INTO tb_consultas (id, usuario, empresa_id, pergunta, resultado_json, data_hora)
            VALUES (?, ?, ?, ?, ?, ?);
        """;

        String sqlAcao = """
            INSERT INTO tb_acoes (id, consulta_id, titulo, descricao, impacto, esforco, prazo, concluida)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlConsulta)) {
                    ps.setString(1, entry.getId());
                    ps.setString(2, entry.getUsuario());
                    ps.setString(3, entry.getEmpresaId());
                    ps.setString(4, entry.getPerguntaOriginal());
                    ps.setString(5, mapper.writeValueAsString(entry.getResultado()));
                    ps.setString(6, entry.getDataHora() != null ? entry.getDataHora().toString() : LocalDateTime.now().toString());
                    ps.executeUpdate();
                }

                // Salva itens de ação associados à consulta
                if (entry.getResultado() != null && entry.getResultado().getAcoesPriorizadas() != null) {
                    try (PreparedStatement psAcao = conn.prepareStatement(sqlAcao)) {
                        for (ActionItem action : entry.getResultado().getAcoesPriorizadas()) {
                            psAcao.setString(1, action.getId());
                            psAcao.setString(2, entry.getId());
                            psAcao.setString(3, action.getTitulo());
                            psAcao.setString(4, action.getDescricao());
                            psAcao.setString(5, action.getImpacto());
                            psAcao.setString(6, action.getEsforco());
                            psAcao.setString(7, action.getPrazoSugerido());
                            psAcao.setBoolean(8, action.isConcluida());
                            psAcao.addBatch();
                        }
                        psAcao.executeBatch();
                    }
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao persistir histórico de consultoria: " + e.getMessage(), e);
        }
    }

    public List<ConsultationHistoryEntry> listarPorUsuario(String usuario) {
        List<ConsultationHistoryEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_consultas WHERE LOWER(usuario) = LOWER(?) ORDER BY data_hora DESC;";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntry(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar consultas do usuário: " + e.getMessage(), e);
        }
        return list;
    }

    public Optional<ConsultationHistoryEntry> buscarPorId(String id) {
        String sql = "SELECT * FROM tb_consultas WHERE id = ?;";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntry(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar consulta por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private ConsultationHistoryEntry mapResultSetToEntry(ResultSet rs) throws Exception {
        ConsultationHistoryEntry entry = new ConsultationHistoryEntry();
        entry.setId(rs.getString("id"));
        entry.setUsuario(rs.getString("usuario"));
        entry.setEmpresaId(rs.getString("empresa_id"));
        entry.setPerguntaOriginal(rs.getString("pergunta"));
        String json = rs.getString("resultado_json");
        if (json != null && !json.isBlank()) {
            StructuredDiagnosis diag = mapper.readValue(json, StructuredDiagnosis.class);
            entry.setResultado(diag);
        }
        String dt = rs.getString("data_hora");
        if (dt != null) {
            entry.setDataHora(LocalDateTime.parse(dt));
        }
        return entry;
    }
}
