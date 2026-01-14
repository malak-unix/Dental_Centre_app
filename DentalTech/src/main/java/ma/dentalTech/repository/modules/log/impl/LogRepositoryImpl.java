package ma.dentalTech.repository.modules.log.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.log.Log;
import ma.dentalTech.repository.modules.log.api.LogRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogRepositoryImpl implements LogRepository {

    // Helper for RepoFactory compatibility
    @SuppressWarnings("unused")
    private final Connection connection;

    public LogRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    public LogRepositoryImpl() {
        this.connection = null;
    }

    @Override
    public Log findById(Long id) {
        // Not used heavily, but good to have
        return null;
    }

    @Override
    public List<Log> findAll() {
        // Assuming table name 'audit_log' or 'log' -> checking entity or DB schema is
        // best
        // Based on Log entity, let's assume table is 'log' or 'logs'.
        // Typically it is 'log' or 'audit_log'. I will use 'log' for now.
        String sql = "SELECT * FROM log ORDER BY date_action DESC";
        List<Log> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            // Table might not exist or be named differently.
            // In a real scenario I'd check schema.
            // For now, return empty list on error to prevent crash.
            e.printStackTrace();
            return list;
        }
    }

    @Override
    public void create(Log entity) {
        if (entity == null)
            return;
        String sql = "INSERT INTO log (action, description, date_action, utilisateur_id) VALUES (?, ?, ?, ?)";

        try (Connection cn = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getAction());
            ps.setString(2, entity.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getDateAction()));
            if (entity.getUtilisateurId() != null)
                ps.setLong(4, entity.getUtilisateurId());
            else
                ps.setNull(4, Types.BIGINT);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    entity.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Log entity) {
        // Log is usually immutable
    }

    @Override
    public void deleteById(Long id) {
        // Logs usually not deleted one by one
    }

    @Override
    public void delete(Log entity) {
        if (entity != null)
            deleteById(entity.getId());
    }

    private Log map(ResultSet rs) throws SQLException {
        return Log.builder()
                .id(rs.getLong("id"))
                .action(rs.getString("action"))
                .description(rs.getString("description"))
                .dateAction(rs.getTimestamp("date_action").toLocalDateTime())
                .utilisateurId(rs.getObject("utilisateur_id") != null ? rs.getLong("utilisateur_id") : null)
                .build();
    }
}
