import ma.dentalTech.conf.Db;
import java.sql.Connection;

public class TestConnexion {
    public static void main(String[] args) {
        Connection cn = Db.getConnection();
        if (cn != null) {
            System.out.println(" Test OK : la connexion fonctionne !");
        } else {
            System.out.println(" Test échoué : pas de connexion.");
        }
    }
}
