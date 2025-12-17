package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.repository.common.CrudRepository;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

    // C'est cette ligne qui manquait et causait votre erreur :
    Utilisateur findByLogin(String login);

    boolean existsByEmail(String email);
}