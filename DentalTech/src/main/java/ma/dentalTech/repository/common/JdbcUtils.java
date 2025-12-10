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
    private static final Properties props = new Properties();

    static {
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new IOException("Fichier de configuration JDBC introuvable: " + CONFIG_FILE);
            }

            // Charge les propriétés
            props.load(input);

            // Lit le driver, avec une valeur par défaut pour MySQL
            String driverClass = props.getProperty("driver", "com.mysql.cj.jdbc.Driver");
            Class.forName(driverClass);

            System.out.println("[JdbcUtils] Driver chargé : " + driverClass);
            System.out.println("[JdbcUtils] URL utilisée : " + props.getProperty("url"));

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erreur de chargement du driver/config JDBC", e);
        }
    }

    private JdbcUtils() {
        // utilitaire, pas d'instance
    }

    public static Connection getConnection() throws DaoException {
        try {
            String url = props.getProperty("url");
            String user = props.getProperty("username");
            String pass = props.getProperty("password");

            if (url == null || url.isBlank()) {
                throw new DaoException("Propriété 'url' absente ou vide dans " + CONFIG_FILE);
            }

            return DriverManager.getConnection(url, user, pass);

        } catch (SQLException e) {
            throw new DaoException("Erreur de connexion à la base de données : " + e.getMessage(), e);
        }
    }
}
