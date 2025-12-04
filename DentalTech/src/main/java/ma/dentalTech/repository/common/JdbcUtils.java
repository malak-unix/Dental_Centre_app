package ma.dentalTech.repository.common;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.conf.SessionFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utilitaires JDBC communs pour tout le projet.
 * - Fournit une connexion via SessionFactory
 * - Méthodes utilitaires pour fermer / rollback proprement
 */
public final class JdbcUtils {

    private JdbcUtils() {
        // utilitaire : pas d'instance
    }

    /**
     * Récupère une connexion JDBC en s'appuyant sur SessionFactory.
     */
    public static Connection getConnection() throws DaoException {
        try {
            return SessionFactory.getInstance().getConnection();
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'obtention de la connexion JDBC", e);
        }
    }

    /**
     * Ferme silencieusement un AutoCloseable (ResultSet, Statement, Connection, ...).
     */
    public static void closeQuietly(AutoCloseable ac) {
        if (ac == null) return;
        try {
            ac.close();
        } catch (Exception ignored) {
            // on ignore l'erreur volontairement
        }
    }

    /**
     * Effectue un rollback silencieux sur une connexion.
     */
    public static void rollbackQuietly(Connection conn) {
        if (conn == null) return;
        try {
            conn.rollback();
        } catch (SQLException ignored) {
            // on ignore aussi ici
        }
    }
}
