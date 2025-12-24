package ma.dentalTech.service.modules.users.api;
import ma.dentalTech.entities.admin.Admin;
import java.util.List;

public interface AdminService {
    List<Admin> getAllAdmins();
    void creerAdmin(Admin admin);
    void supprimerAdmin(Long id);
}