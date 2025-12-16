package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.repository.common.CrudRepository;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {
    Utilisateur findByLogin(String login);
    boolean existsByEmail(String email);
}