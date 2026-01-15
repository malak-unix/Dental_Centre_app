package ma.dentalTech.configuration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseInitializer {

    public static void initialize() {
        System.out.println("--- Vérification du seeding Database (seed.sql) ---");
        
        // On vérifie d'abord si la DB est vide pour éviter d'écraser ou de dupliquer
        // (Approche simple : on check si la table utilisateur a des données)
        if (!isDatabaseEmpty()) {
            System.out.println("✅ Database déjà initialisée (données trouvées). Skip seeding.");
            return;
        }

        System.out.println("⚡ Database vide détectée. Exécution de seed.sql...");
        runSeedScript();
    }

    private static boolean isDatabaseEmpty() {
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement s = c.createStatement();
             var rs = s.executeQuery("SELECT count(*) FROM utilisateur")) {
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception e) {
            // Si la table n'existe pas encore ou autre erreur, on peut supposer qu'il faut init
            System.err.println("⚠️ Warning check DB empty: " + e.getMessage());
        }
        return true; 
    }

    private static void runSeedScript() {
        try (InputStream in = DatabaseInitializer.class.getResourceAsStream("/dataBase/seed.sql")) {
            if (in == null) {
                System.err.println("❌ fichier /dataBase/seed.sql introuvable dans le classpath.");
                return;
            }

            // Lecture du fichier entier
            String sqlScript;
            try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
                sqlScript = scanner.useDelimiter("\\A").next();
            }

            // Exécution
            try (Connection c = SessionFactory.getInstance().getConnection();
                 Statement s = c.createStatement()) {
                
                // On découpe par instruction (si le driver ne supporte pas allowMultiQueries=true)
                // Mais avec JDBC mysql standard, il vaut mieux souvent exécuter bloc par bloc
                // Ici on fait une approche simple : split par ";"
                
                String[] statements = sqlScript.split(";");
                int count = 0;
                for (String stmt : statements) {
                    String cleanStmt = stmt.trim();
                    if (cleanStmt.isEmpty()) continue;
                    
                    try {
                        s.execute(cleanStmt);
                        count++;
                    } catch (Exception ex) {
                        System.err.println("⚠️ Erreur sur instruction SQL : " + cleanStmt.substring(0, Math.min(cleanStmt.length(), 50)) + "...");
                        System.err.println("   -> " + ex.getMessage());
                    }
                }
                System.out.println("✅ Seeding terminé (" + count + " instructions exécutées).");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur critique lors de l'exécution de seed.sql : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
