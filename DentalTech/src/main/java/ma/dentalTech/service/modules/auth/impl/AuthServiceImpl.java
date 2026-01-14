package ma.dentalTech.service.modules.auth.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import ma.dentalTech.common.utilitaire.RepoFactory;
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
import ma.dentalTech.service.modules.users.api.UserAuthQueryService;
import ma.dentalTech.service.modules.users.api.UserAuthQueryService.UserAuthData;
import ma.dentalTech.service.modules.users.impl.UserAuthQueryServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserAuthQueryService userAuthQuery;
    private final LoginFormValidator validator;
    private final PasswordEncoder passwordEncoder;

    /**
     * 🔁 Constructeur de compatibilité (pour l'UI existante: LoginFrame)
     * Ancienne signature => on délègue vers la nouvelle architecture.
     */
    public AuthServiceImpl(
            RepoFactory<UtilisateurRepository> userRepoFactory,
            RepoFactory<RoleRepository> roleRepoFactory,
            LoginFormValidator validator,
            PasswordEncoder passwordEncoder
    ) {
        this(
                new UserAuthQueryServiceImpl(userRepoFactory, roleRepoFactory),
                validator,
                passwordEncoder
        );
    }

    @Override
    public AuthResultDTO authenticate(AuthRequestDTO request) {

        // Sécurité : request null
        if (request == null) {
            return AuthResultDTO.failure("Formulaire invalide");
        }

        // Validation formulaire
        Map<String, String> errors = validator != null ? validator.validate(request) : null;
        if (errors != null && !errors.isEmpty()) {
            return AuthResultDTO.failure("Formulaire invalide", errors);
        }

        String login = request.login();
        String password = request.password();

        if (login == null || login.isBlank() || password == null || password.isBlank()) {
            return AuthResultDTO.failure("Formulaire invalide");
        }

        // ✅ PROTECTION DB + message utilisateur introuvable
        UserAuthData authData;
        try {
            authData = userAuthQuery.loadByLogin(login);
        } catch (RuntimeException e) {
            // DB down OU erreur transaction → comportement fonctionnel attendu
            return AuthResultDTO.failure("Authentification échouée :: Utilisateur introuvable");
        }

        if (authData == null || authData.utilisateur() == null) {
            return AuthResultDTO.failure("Authentification échouée :: Utilisateur introuvable");
        }

        Utilisateur user = authData.utilisateur();
        String stored = user.getMotDePasse();

        // DEV MODE: accepte clair OU hash
        boolean ok = false;

        if (stored != null) {
            // 1) compare en clair (DEV)
            ok = stored.equals(password);

            // 2) sinon compare via encoder (prod)
            if (!ok && passwordEncoder != null) {
                try {
                    ok = passwordEncoder.matches(password, stored);
                } catch (Exception ignored) {
                    // ignore si stored n'est pas un hash compatible
                }
            }
        }

        if (!ok) {
            return AuthResultDTO.failure("Mot de passe incorrect");
        }


        UserPrincipalDTO principal = buildUserPrincipal(user, authData.roles());
        return AuthResultDTO.success(principal);
    }


    private UserPrincipalDTO buildUserPrincipal(Utilisateur u, List<Role> roles) {

        Set<LibelleRole> roleTypes = roles == null ? new LinkedHashSet<>() :
                roles.stream()
                        .filter(Objects::nonNull)
                        .map(Role::getLibelle)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> privileges = roles == null ? new LinkedHashSet<>() :
                roles.stream()
                        .filter(Objects::nonNull)
                        .map(Role::getPrivileges)
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
