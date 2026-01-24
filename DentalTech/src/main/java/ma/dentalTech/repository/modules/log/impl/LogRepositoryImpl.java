package ma.dentalTech.repository.modules.log.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.log.Log;
import ma.dentalTech.repository.modules.log.api.LogRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogRepositoryImpl implements LogRepository {

    @Override
    public void create(Log log) {
        if (log == null) throw new DaoException("Log null");

        String sql = """
            INSERT INTO logs
              (utilisateur_id, date_log, entite_attribue, action, description,
               date_creation, date_modification, cree_par, modifie_par)
            VALUES (?, NOW(), ?, ?, ?, NOW(), NOW(), ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (log.getUtilisateurId() != null) ps.setLong(1, log.getUtilisateurId());
            else ps.setNull(1, Types.BIGINT);

            ps.setString(2, null);
            ps.setString(3, log.getAction());
            ps.setString(4, log.getDescription());
            ps.setString(5, log.getCreePar());
            ps.setString(6, log.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) log.setId(keys.getLong(1));
            }
        } catch (Exception e) {
            throw new DaoException("Erreur create(Log)", e);
        }
    }

    @Override
    public List<Log> findRecent(int limit) {
        int lim = (limit <= 0) ? 10 : limit;
        String sql = "SELECT * FROM logs ORDER BY date_log DESC, id DESC LIMIT ?";
        List<Log> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, lim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapLog(rs));
                }
            }
        } catch (Exception e) {
            throw new DaoException("Erreur findRecent(Log)", e);
        }
        return list;
    }

    private Log mapLog(ResultSet rs) throws SQLException {
        Log l = new Log();
        l.setId(rs.getLong("id"));
        long uid = rs.getLong("utilisateur_id");
        if (!rs.wasNull()) l.setUtilisateurId(uid);

        Timestamp ts = rs.getTimestamp("date_log");
        if (ts != null) l.setDateAction(ts.toLocalDateTime());

        l.setAction(rs.getString("action"));
        l.setDescription(rs.getString("description"));

        Timestamp dc = rs.getTimestamp("date_creation");
        if (dc != null) l.setDateCreation(dc.toLocalDateTime());
        Timestamp dm = rs.getTimestamp("date_modification");
        if (dm != null) l.setDateDerniereModification(dm.toLocalDateTime());

        l.setCreePar(rs.getString("cree_par"));
        l.setModifiePar(rs.getString("modifie_par"));
        return l;
    }
}
