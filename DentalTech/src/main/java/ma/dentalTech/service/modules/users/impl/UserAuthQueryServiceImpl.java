package ma.dentalTech.service.modules.users.impl;

import lombok.AllArgsConstructor;
import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.common.utilitaire.Transaction;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.users.api.UserAuthQueryService;

import java.util.List;

@AllArgsConstructor
public class UserAuthQueryServiceImpl implements UserAuthQueryService {

    private final RepoFactory<UtilisateurRepository> userRepoFactory;
    private final RepoFactory<RoleRepository> roleRepoFactory;

    @Override
    public UserAuthData loadByLogin(String login) {
        if (login == null || login.isBlank()) return null;

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = userRepoFactory.create(cnx);
            RoleRepository roleRepo = roleRepoFactory.create(cnx);

            Utilisateur user = userRepo.findByLogin(login).orElse(null);
            if (user == null) return null;

            List<Role> roles = roleRepo.findRolesByUtilisateurId(user.getId());
            return new UserAuthData(user, roles);
        });
    }
}
