package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.admin.Admin;
import ma.dentalTech.repository.modules.users.api.AdminRepository;
import ma.dentalTech.repository.modules.users.impl.AdminRepositoryImpl;
import ma.dentalTech.service.modules.users.api.AdminService; // Import de l'interface

import java.util.List;

public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepo = new AdminRepositoryImpl();

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepo.findAll();
    }

    @Override
    public void creerAdmin(Admin admin) {
        // Validation : Un admin doit avoir un login et un mot de passe
        if (admin.getLogin() == null || admin.getMotDePass_hash() == null) {
            throw new RuntimeException("Login et mot de passe obligatoires pour un Admin.");
        }

        // On sauvegarde
        adminRepo.create(admin);
        System.out.println("Nouvel administrateur créé : " + admin.getNom());
    }

    @Override
    public void supprimerAdmin(Long id) {
        // On pourrait ajouter une sécurité : "Impossible de supprimer le dernier admin"
        adminRepo.deleteById(id);
    }
}