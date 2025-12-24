package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.utilisateur.Utilisateur;
import java.util.Optional;

public interface UtilisateurRepository {
    Optional<Utilisateur> findByLogin(String login);
    void updateLastLogin(Long userId);
    Utilisateur save(Utilisateur utilisateur);
}