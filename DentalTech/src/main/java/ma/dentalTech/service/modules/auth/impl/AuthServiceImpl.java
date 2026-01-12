package ma.dentalTech.service.modules.auth.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.common.utilitaire.Transaction;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.LoginFormValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RepoFactory<UtilisateurRepository> userRepoFactory;
    private final RepoFactory<RoleRepository> roleRepoFactory;
    private final LoginFormValidator validator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResultDTO authenticate(AuthRequestDTO request) {
        Map<String, String> errors = validator.validate(request);

        if (!errors.isEmpty()) {
            return AuthResultDTO.failure("Formulaire invalide", errors);
        }

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = userRepoFactory.create(cnx);
            RoleRepository roleRepo = roleRepoFactory.create(cnx);

            Utilisateur user = userRepo.findByLogin(request.login()).orElse(null);
            if (user == null) {
                return AuthResultDTO.failure("Authentification échouée :: Utilisateur introuvable");
            }

            boolean ok = passwordEncoder.matches(request.password(), user.getMotDePasse());
            if (!ok) {
                return AuthResultDTO.failure("Mot de passe incorrect");
            }

            UserPrincipalDTO principal = buildUserPrincipal(user, roleRepo);
            return AuthResultDTO.success(principal);
        });
    }

    private UserPrincipalDTO buildUserPrincipal(Utilisateur u, RoleRepository roleRepo) {
        List<Role> roles = roleRepo.findRolesByUtilisateurId(u.getId());

        // Roles -> Set<LibelleRole>
        Set<LibelleRole> roleTypes = roles.stream()
                .filter(Objects::nonNull)
                .map(Role::getLibelle) // ✅ anciennement getType()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Roles -> Set<String> privileges (CSV -> split)
        Set<String> privileges = roles.stream()
                .filter(Objects::nonNull)
                .map(Role::getPrivileges) // ✅ String CSV
                .filter(Objects::nonNull)
                .flatMap(p -> Arrays.stream(p.split(",")))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        LibelleRole rolePrincipal = roleTypes.stream().findFirst().orElse(null);

        return new UserPrincipalDTO(
                u.getId(),
                u.getNom(),
                u.getEmail(),
                u.getLogin(),
                rolePrincipal,
                roleTypes,
                privileges
        );
    }
}
