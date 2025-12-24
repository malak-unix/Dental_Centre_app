package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.utilisateur.Utilisateur;
import java.util.List;

public interface UtilisateurService {
    List<Utilisateur> getAllUtilisateurs();
    Utilisateur getUtilisateurParId(Long id);
    void creerUtilisateur(Utilisateur u);
    void supprimerUtilisateur(Long id);
    // On peut ajouter la mise à jour si besoin
    void modifierUtilisateur(Utilisateur u);
}