package ma.dentalTech.conf;

import lombok.Getter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;

public class Db {

    @Getter
    private static Connection connection;

    static {
        try {
            Properties props = new Properties();

            // Chargement du fichier dans /resources/config
            InputStream input = Db.class.getClassLoader()
                    .getResourceAsStream("config/db.properties");

            if (input == null) {
                throw new RuntimeException("Fichier db.properties introuvable dans resources/config !");
            }

            props.load(input);

            String url = props.getProperty("url");
            String user = props.getProperty("username");
            String pass = props.getProperty("password");

            if (url == null) {
                throw new RuntimeException("La propriété 'url' est manquante dans db.properties !");
            }

            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connexion réussie à MySQL !");
        } catch (Exception e) {
            System.err.println("❌ Erreur de connexion MySQL : " + e.getMessage());
        }
    }
}
