package ma.dentalTech.repository.modules.users.impl;
//fais par Aya mais j ai la laisser des logs à faire pour responsable de ce module
import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import java.sql.*;
import java.time.LocalDate;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    @Override
    public String findRoleByUtilisateurId(Long utilisateurId) throws DaoException {
        String sql = "SELECT role FROM utilisateur WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("role") : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur findRoleByUtilisateurId, id=" + utilisateurId, e);
        }
    }

    @Override
    public Integer countAll() throws DaoException {
        String sql = "SELECT COUNT(*) c FROM utilisateur";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("c") : 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur countAll utilisateurs", e);
        }
    }

    @Override
    public Integer countByRole(String role) throws DaoException {
        String sql = "SELECT COUNT(*) c FROM utilisateur WHERE role = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("c") : 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur countByRole role=" + role, e);
        }
    }

    @Override
    public Integer countConnexionsJour(LocalDate date) throws DaoException {
        // adapte selon ta table de logs si elle existe.
        // fallback simple (0 si pas de table)
        return 0;
    }
}
