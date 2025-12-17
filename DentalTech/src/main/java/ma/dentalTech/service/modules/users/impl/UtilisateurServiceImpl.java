package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.repository.modules.users.impl.UtilisateurRepositoryImpl;
import ma.dentalTech.service.modules.users.api.UtilisateurService;

import java.util.List;

public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepo = new UtilisateurRepositoryImpl();

    @Override
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepo.findAll();
    }

    @Override
    public Utilisateur getUtilisateurParId(Long id) {
        Utilisateur u = utilisateurRepo.findById(id);
        if (u == null) {
            throw new RuntimeException("Erreur : Utilisateur introuvable (ID " + id + ")");
        }
        return u;
    }

    @Override
    public void creerUtilisateur(Utilisateur u) {
        // --- REGLE METIER 1 : Email Unique ---
        // On vérifie si un utilisateur existe déjà avec cet email
        Utilisateur existant = utilisateurRepo.findByLogin(u.getLogin()); // Ou findByEmail selon ton repo
        if (existant != null) {
            throw new RuntimeException("Erreur : Ce login est déjà utilisé !");
        }

        // --- REGLE METIER 2 : Mot de passe ---
        if (u.getMotDePass_hash() == null || u.getMotDePass_hash().length() < 4) {
            throw new RuntimeException("Erreur : Le mot de passe est trop court (min 4 caractères).");
        }

        utilisateurRepo.create(u);
        System.out.println("Service : Utilisateur " + u.getNom() + " créé avec succès.");
    }

    @Override
    public void modifierUtilisateur(Utilisateur u) {
        // Vérifier l'existence avant de modifier
        if (utilisateurRepo.findById(u.getId()) == null) {
            throw new RuntimeException("Impossible de modifier : ID introuvable.");
        }
        utilisateurRepo.update(u);
    }

    @Override
    public void supprimerUtilisateur(Long id) {
        if (utilisateurRepo.findById(id) == null) {
            throw new RuntimeException("Erreur : Impossible de supprimer, ID inexistant.");
        }
        utilisateurRepo.deleteById(id);
    }
}