package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.mvc.dto.auth.*; // Utilisation de tes DTOs

public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UtilisateurRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResultDTO authenticate(AuthRequestDTO request) {
        // 1. Appel du repo de mes collègues
        return userRepository.findByLogin(request.getLogin())
                .map(user -> {
                    // 2. Comparaison avec le champ motDePass_hash de leur entité
                    if (passwordEncoder.matches(request.getPassword(), user.getMotDePasse())) {

                        // 3. Construction du DTO de sortie
                        UserPrincipalDTO principal = UserPrincipalDTO.builder()
                                .id(user.getId())
                                .login(user.getLogin())
                                .nom(user.getNom())
                                .prenom(user.getPrenom())
                                // On récupère le premier rôle de la liste s'il existe
                                .role(user.getRoles() != null && !user.getRoles().isEmpty()
                                        ? LibelleRole.valueOf(user.getRoles().get(0).getLibelle())
                                        : null)
                                .build();
                        return new AuthResultDTO(true, "Connexion réussie", principal);
                    }
                    return new AuthResultDTO(false, "Mot de passe incorrect", null);
            })
                .orElse(new AuthResultDTO(false, "Utilisateur introuvable", null));
    }

    @Override
    public UserPrincipalDTO loadUserPrincipalByLogin(String login) { return null; }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) { }
}