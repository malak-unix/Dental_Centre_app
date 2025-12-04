package ma.dentalTech.conf;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fabrique centrale pour obtenir des connexions JDBC.
 * Lit la configuration dans src/main/resources/config/db.properties.
 */
public class SessionFactory {

    private static SessionFactory INSTANCE;

    private final String url;
    private final String username;
    private final String password;

    private SessionFactory() {
        try {
            // Chargement du fichier de config
            Properties props = new Properties();
            try (InputStream in = getClass()
                    .getClassLoader()
                    .getResourceAsStream("config/db.properties")) {

                if (in == null) {
                    throw new IllegalStateException("Fichier config/db.properties introuvable dans le classpath");
                }
                props.load(in);
            }

            // Récupération des propriétés
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.user", props.getProperty("db.username"));
            this.password = props.getProperty("db.password");

            // Nom du driver (plusieurs clés possibles selon la config)
            String driver = props.getProperty("db.driver");
            if (driver == null || driver.isBlank()) {
                driver = props.getProperty("db.driver-class-name", "com.mysql.cj.jdbc.Driver");
            }

            // Chargement du driver JDBC
            Class.forName(driver);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement de la configuration DB", e);
        }
    }

    /**
     * Singleton
     */
    public static SessionFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionFactory();
        }
        return INSTANCE;
    }

    /**
     * Retourne une nouvelle connexion JDBC.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
