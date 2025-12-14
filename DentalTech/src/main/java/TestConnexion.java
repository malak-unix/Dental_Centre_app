import ma.dentalTech.configuration.SessionFactory;

import java.sql.Connection;

public class TestConnexion {

    public static void main(String[] args) {

        try (Connection cn = SessionFactory.getInstance().getConnection()) {

            if (cn != null && !cn.isClosed()) {
                System.out.println("✅ Connexion OK via SessionFactory");
            } else {
                System.out.println("❌ Connexion échouée");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
