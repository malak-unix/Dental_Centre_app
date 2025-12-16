package ma.dentalTech.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

import ma.dentalTech.configuration.util.PropertiesExtractor;

/**
 * Classe responsable de la création et de la gestion d'une connexion JDBC unique.
 *
 * Pattern utilisé : Singleton (avec double-checked locking)
 * ------------------------------------------------------------
 * - Une seule instance de SessionFactory pour toute l'application
 * - Une seule connexion JDBC réutilisée
 * - Thread-safe (sécurité multi-thread)
 *
 * Principe :
 * -------------
 * - getInstance() → retourne toujours la même fabrique unique
 * - getConnection() → retourne une connexion valide (et la recrée si besoin)
 */

public final class SessionFactory {

    private static volatile SessionFactory instance;
    /** Objet connexion JDBC unique */
    private Connection connection;
    /** Propriétés de configuration (fichier .properties) */
    private static final String PROPS_PATH = "config/db.properties";
    private static final String URL_KEY    = "jdbc.url";
    private static final String USER_KEY   = "jdbc.username";
    private static final String PASS_KEY   = "jdbc.password";
    private static final String DRIVER_KEY = "jdbc.driver";

    /** Valeurs lues depuis le fichier de configuration */
    private String url;
    private String user;
    private String password;
    private String driver;

    /**
     * Constructeur privé → empêche toute instanciation directe.
     * Initialise la configuration et le driver JDBC.
     */
    private SessionFactory() {
        var properties = PropertiesExtractor.loadConfigFile(PROPS_PATH);
        this.url = PropertiesExtractor.getPropertyValue(URL_KEY, properties);
        this.user = PropertiesExtractor.getPropertyValue(USER_KEY, properties);
        this.password = PropertiesExtractor.getPropertyValue(PASS_KEY, properties);
        this.driver = PropertiesExtractor.getPropertyValue(DRIVER_KEY, properties);
        // Charger explicitement le driver si défini (bonne pratique)
        if (driver != null && !driver.isBlank()) {
            try {
                Class.forName(driver);
                System.out.println(" Driver JDBC chargé avec succès : " + driver);
            } catch (ClassNotFoundException e) {
                System.err.println(" Driver JDBC introuvable : " + driver);
            }
        }
    }

    /**
     * Retourne l'unique instance du Singleton.
     * Utilise le pattern "Double Checked Locking" pour être thread safe.
     * Car au moment de verrouillage, un autre Thread pourrait créer déjà l'instance
     */
    public static SessionFactory getInstance() {
        if (instance == null) {
            synchronized (SessionFactory.class) {
                if (instance == null) {
                    instance = new SessionFactory();
                }
            }
        }
        return instance;
    }

    /**
     * Retourne une connexion JDBC active et valide.
     * Si la connexion n'existe pas, est fermée ou n'est plus valide, elle est recréée.
     *
     * @return une instance valide de {@link Connection}.
     * @throws SQLException en cas d'erreur de création.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !isValid(connection)) {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println(" Nouvelle connexion JDBC établie avec succès !");
        }
        return connection;
    }

    /**
     * Vérifie si la connexion est encore valide.
     *
     * @param conn la connexion à tester
     * @return true si la connexion est encore utilisable, sinon false
     */
    private boolean isValid(Connection conn) {
        try {
            return conn != null && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Ferme proprement la connexion si elle est ouverte.
     * À appeler à la fermeture de l'application.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion JDBC fermée proprement.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
