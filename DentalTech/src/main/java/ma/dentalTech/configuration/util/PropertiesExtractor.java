package ma.dentalTech.configuration.util;

import java.io.IOException; //exception si lecture échoue.
import java.io.InputStream; //un flux pour lire un fichier “en bytes”.
import java.util.Properties; //classe Java standard pour gérer les fichiers .properties.
/*Méthode statique : on appelle PropertiesExtractor.loadConfigFile(...).
Elle reçoit le chemin du fichier dans les resources (ex: "config/db.properties").
Elle retourne un objet Properties rempli.
*/
public class PropertiesExtractor {

    public static String CONFIG_PATH; //Variable globale (static) qui mémorise le chemin du dernier fichier chargé.


    public static Properties loadConfigFile(String PROPS_PATH) {

        CONFIG_PATH = PROPS_PATH;
        Properties properties = new Properties();

        try (InputStream in = Thread.currentThread() //récupère le thread courant.
                .getContextClassLoader() //récupère le classloader qui sait charger des ressources du projet (resources).
                .getResourceAsStream(PROPS_PATH)) { //ouvre un flux de lecture vers le fichier situé dans le classpath.
            if (in == null)  //Si le fichier n’existe pas dans resources, getResourceAsStream renvoie null.
                throw new IllegalStateException("config file not found: " + PROPS_PATH);
            properties.load(in);
            return properties;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture " + PROPS_PATH, e);
        }
    }

    public static String getPropertyValue(String key, Properties properties) {

        String v = properties.getProperty(key);
        if (v == null) {
            throw new IllegalStateException("property key not found : " + CONFIG_PATH + " : " + key);
        }
        return v.trim();
    }
}

