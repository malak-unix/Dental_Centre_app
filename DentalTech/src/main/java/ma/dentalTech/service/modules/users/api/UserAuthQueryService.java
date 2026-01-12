package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Utilisateur;

import java.util.List;

/**
 * Service read-only pour fournir à AUTH les infos nécessaires.
 */
public interface UserAuthQueryService {

    UserAuthData loadByLogin(String login);

    record UserAuthData(Utilisateur utilisateur, List<Role> roles) {}
}
