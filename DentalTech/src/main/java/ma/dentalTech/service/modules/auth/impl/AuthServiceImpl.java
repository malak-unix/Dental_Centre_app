package ma.dentalTech.service.modules.auth.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
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

// Voici les imports corrigés vers ton dossier 'utilitaire'
import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.common.utilitaire.Transaction;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data @AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RepoFactory<UtilisateurRepository> userRepoFactory;
    private final RepoFactory<RoleRepository> roleRepoFactory;
    private final LoginFormValidator validator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResultDTO authenticate(AuthRequestDTO request) {
        // 1. Validation du formulaire (syntaxe Record : .login())
        Map<String, String> errors = validator.validate(request);

        if (!errors.isEmpty()) {
            return AuthResultDTO.failure("Formulaire invalide", errors);
        }

        // 2. Initialisation de la transaction
        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = userRepoFactory.create(cnx);
            RoleRepository roleRepo = roleRepoFactory.create(cnx);

            // 3. Recherche de l'utilisateur (gère l'Optional de ton repo)
            Utilisateur user = userRepo.findByLogin(request.login()).orElse(null);

            if (user == null) {
                return AuthResultDTO.failure("Authentification échouée :: Utilisateur introuvable");
            }

            // 4. Vérification du mot de passe
            boolean ok = passwordEncoder.matches(request.password(), user.getMotDePasse());
            if (!ok) {
                return AuthResultDTO.failure("Mot de passe incorrect");
            }

            // 5. Construction du principal pour la session
            UserPrincipalDTO principal = buildUserPrincipal(user, roleRepo);

            return AuthResultDTO.success(principal);
        });
    }

    private UserPrincipalDTO buildUserPrincipal(Utilisateur u, RoleRepository roleRepo) {
        // Récupérer les rôles affectés à l'utilisateur
        List<Role> roles = roleRepo.findRolesByUtilisateurId(u.getId());

        // Transformer en set de LibelleRole (ton Enum)
        Set<LibelleRole> roleTypes = roles.stream()
                .map(Role::getType)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Récupérer les privilèges affectés aux rôles
        Set<String> privileges = roles.stream()
                .filter(Objects::nonNull)
                .flatMap(r -> r.getPrivileges() != null ? r.getPrivileges().stream() : Stream.empty())
                .collect(Collectors.toSet());

        // Le premier rôle est considéré comme rôle principal
        LibelleRole rolePrincipal = roleTypes.stream().findFirst().orElse(null);

        return new UserPrincipalDTO(
                u.getId(), u.getNom(), u.getEmail(), u.getLogin(),
                rolePrincipal, roleTypes, privileges
        );
    }
}