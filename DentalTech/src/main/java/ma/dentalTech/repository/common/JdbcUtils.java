package ma.dentalTech.repository.common;

import ma.dentalTech.common.exceptions.DaoException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class JdbcUtils {

    private static final String CONFIG_FILE = "config/db.properties";
    private static Properties props = new Properties();

    static {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IOException("Fichier de configuration JDBC introuvable: " + CONFIG_FILE);
            }
            props.load(input);
            Class.forName(props.getProperty("driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erreur de chargement du driver/config JDBC", e);
        }
    }

    private JdbcUtils() { }

    public static Connection getConnection() throws DaoException {
        try {
            return DriverManager.getConnection(
                    props.getProperty("url"),
                    props.getProperty("username"),
                    props.getProperty("password")
            );
        } catch (SQLException e) {
            throw new DaoException("Erreur de connexion à la base de données : " + e.getMessage(), e);
        }
    }
}