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

            InputStream input = Db.class.getClassLoader()
                    .getResourceAsStream("config/db.properties");

            if (input == null) {
                throw new RuntimeException("Fichier db.properties introuvable dans resources/config !");
            }

            props.load(input);

            String url = props.getProperty("jdbc.url");
            String user = props.getProperty("jdbc.username");
            String pass = props.getProperty("jdbc.password");

            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connexion réussie à MySQL !");
        } catch (Exception e) {
            System.err.println("❌ Erreur de connexion MySQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
