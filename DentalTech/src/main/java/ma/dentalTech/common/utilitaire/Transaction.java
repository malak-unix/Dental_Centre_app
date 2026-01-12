package ma.dentalTech.common.utilitaire;

import ma.dentalTech.configuration.SessionFactory;
import java.sql.Connection;
import java.util.function.Function;

public class Transaction {

    public static <R> R initTransaction(Function<Connection, R> action) {
        Connection cnx = null;
        try {
            // On utilise l'instance unique (Singleton) de la SessionFactory
            // Généralement la méthode s'appelle getInstance()
            cnx = SessionFactory.getInstance().getConnection();

            return action.apply(cnx);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de transaction : " + e.getMessage(), e);
        } finally {
            if (cnx != null) {
                try { cnx.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
}