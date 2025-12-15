package ma.dentalTech.repository.common;

import ma.dentalTech.configuration.SessionFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Bridge utilitaire pour l'ancien code jdbc_implementation.
 * Il délègue vers SessionFactory (nouvelle archi).
 */
public final class JdbcUtils {

    private JdbcUtils() {}

    public static Connection getConnection() throws SQLException {
        return SessionFactory.getInstance().getConnection();
    }
}
