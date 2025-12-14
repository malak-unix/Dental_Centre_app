package ma.dentalTech.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public final class SessionFactory {

    private static SessionFactory instance;

    private String url;
    private String user;
    private String password;
    private String driver;

    // constructeur privé (singleton)
    private SessionFactory() {
        loadConfiguration();
        loadDriver();
    }

    // Singleton
    public static synchronized SessionFactory getInstance() {
        if (instance == null) {
            instance = new SessionFactory();
        }
        return instance;
    }

    // Lire db.properties
    private void loadConfiguration() {
        try {
            Properties props = new Properties();

            try (InputStream in = SessionFactory.class
                    .getResourceAsStream("/config/db.properties")) {

                if (in == null) {
                    throw new RuntimeException("Fichier /config/db.properties introuvable");
                }

                props.load(in);
            }

            this.url = props.getProperty("datasource.url");
            this.user = props.getProperty("datasource.user");
            this.password = props.getProperty("datasource.password");
            this.driver = props.getProperty("datasource.driver");

            if (url == null || user == null || driver == null) {
                throw new RuntimeException("Paramètres DB manquants dans db.properties");
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement configuration DB", e);
        }
    }

    // Charger le driver JDBC
    private void loadDriver() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC introuvable : " + driver, e);
        }
    }

    // Méthode utilisée par les repositories
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
