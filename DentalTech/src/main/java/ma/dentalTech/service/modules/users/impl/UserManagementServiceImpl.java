package ma.dentalTech.service.modules.users.impl;

import lombok.AllArgsConstructor;
import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.common.utilitaire.Transaction;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.*;
import ma.dentalTech.mvc.dto.users.*;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.users.api.UserManagementService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Users: création / recherche / update.
 *
 * Note: les repositories actuels ouvrent leur propre connexion (SessionFactory).
 * Transaction.initTransaction est conservé pour rester cohérent avec l'architecture.
 */
@AllArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final RepoFactory<UtilisateurRepository> utilisateurRepoFactory;
    private final RepoFactory<RoleRepository> roleRepoFactory;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserSummaryDTO createAdmin(CreateAdminRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("request null");

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);

            Admin admin = new Admin();
            admin.setNom(trim(request.getNom()));
            admin.setPrenom(trim(request.getPrenom()));
            admin.setLogin(trim(request.getLogin()));
            admin.setEmail(buildEmailFromLogin(request.getLogin()));
            admin.setMotDePasse(passwordEncoder.encode(request.getPassword()));
            admin.setActif(true);

            userRepo.create(admin);

            // ✅ sécuriser l'id (certaines impl utilisent RETURN_GENERATED_KEYS)
            Utilisateur created = (admin.getId() != null)
                    ? admin
                    : userRepo.findByLogin(admin.getLogin()).orElse(admin);

            assignRoleInternal(cnx, created.getId(), LibelleRole.ADMIN);
            return mapToSummary(created);
        });
    }

    @Override
    public UserSummaryDTO createMedecin(CreateMedecinRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("request null");

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);

            Medecin m = new Medecin();
            m.setNom(trim(request.getNom()));
            m.setPrenom(trim(request.getPrenom()));
            m.setLogin(trim(request.getLogin()));
            m.setEmail(buildEmailFromLogin(request.getLogin()));
            m.setMotDePasse(passwordEncoder.encode(request.getPassword()));
            m.setActif(true);
            m.setSpecialite(trim(request.getSpecialite()));

            userRepo.create(m);

            Utilisateur created = (m.getId() != null)
                    ? m
                    : userRepo.findByLogin(m.getLogin()).orElse(m);

            assignRoleInternal(cnx, created.getId(), LibelleRole.MEDECIN);
            return mapToSummary(created);
        });
    }

    @Override
    public UserSummaryDTO createSecretaire(CreateSecretaireRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("request null");

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);

            Secretaire s = new Secretaire();
            s.setNom(trim(request.getNom()));
            s.setPrenom(trim(request.getPrenom()));
            s.setLogin(trim(request.getLogin()));
            s.setEmail(buildEmailFromLogin(request.getLogin()));
            s.setMotDePasse(passwordEncoder.encode(request.getPassword()));
            s.setActif(true);
            s.setNumCNSS(trim(request.getNumCNSS()));

            userRepo.create(s);

            Utilisateur created = (s.getId() != null)
                    ? s
                    : userRepo.findByLogin(s.getLogin()).orElse(s);

            assignRoleInternal(cnx, created.getId(), LibelleRole.SECRETAIRE);
            return mapToSummary(created);
        });
    }

    @Override
    public UserSummaryDTO getUserById(Long id) {
        if (id == null) return null;
        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository repo = utilisateurRepoFactory.create(cnx);
            Utilisateur u = repo.findById(id);
            return u == null ? null : mapToSummary(u);
        });
    }

    @Override
    public List<UserSummaryDTO> getAllUsers() {
        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository repo = utilisateurRepoFactory.create(cnx);
            List<Utilisateur> users = repo.findAll();
            List<UserSummaryDTO> out = new ArrayList<>();
            if (users != null) {
                for (Utilisateur u : users) {
                    if (u != null) out.add(mapToSummary(u));
                }
            }
            return out;
        });
    }

    @Override
    public List<UserSummaryDTO> searchUsersByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>();
        String kw = keyword.trim();

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository repo = utilisateurRepoFactory.create(cnx);
            List<Utilisateur> users = repo.searchByNom(kw);
            List<UserSummaryDTO> out = new ArrayList<>();
            if (users != null) {
                for (Utilisateur u : users) {
                    if (u != null) out.add(mapToSummary(u));
                }
            }
            return out;
        });
    }

    @Override
    public UserSummaryDTO updateUserProfile(Long id, UserSaveRequestDTO request) {
        if (id == null || request == null) return null;

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);
            Utilisateur existing = userRepo.findById(id);
            if (existing == null) return null;

            if (request.getNom() != null && !request.getNom().isBlank()) existing.setNom(trim(request.getNom()));
            if (request.getPrenom() != null && !request.getPrenom().isBlank()) existing.setPrenom(trim(request.getPrenom()));
            if (request.getLogin() != null && !request.getLogin().isBlank()) {
                existing.setLogin(trim(request.getLogin()));
                existing.setEmail(buildEmailFromLogin(request.getLogin()));
            }

            userRepo.update(existing);

            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                userRepo.updatePassword(id, passwordEncoder.encode(request.getPassword()));
            }

            if (request.getRole() != null) {
                replaceRolesInternal(cnx, id, request.getRole());
            }

            return mapToSummary(existing);
        });
    }

    @Override
    public void assignRoleToUser(Long utilisateurId, LibelleRole roleType) {
        Transaction.initTransaction(cnx -> {
            assignRoleInternal(cnx, utilisateurId, roleType);
            return null;
        });
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, LibelleRole roleType) {
        Transaction.initTransaction(cnx -> {
            RoleRepository roleRepo = roleRepoFactory.create(cnx);
            UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);
            Optional<Role> roleOpt = roleRepo.findByType(roleType);
            roleOpt.ifPresent(r -> userRepo.removeRoleFromUser(utilisateurId, r.getId()));
            return null;
        });
    }

    // ===================== Helpers =====================

    private void assignRoleInternal(java.sql.Connection cnx, Long userId, LibelleRole roleType) {
        if (userId == null || roleType == null) return;
        RoleRepository roleRepo = roleRepoFactory.create(cnx);
        UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);
        roleRepo.findByType(roleType).ifPresent(r -> userRepo.addRoleToUser(userId, r.getId()));
    }

    /**
     * Modèle: 1 utilisateur = 1 rôle principal
     */
    private void replaceRolesInternal(java.sql.Connection cnx, Long userId, LibelleRole newRole) {
        if (userId == null || newRole == null) return;
        UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);
        RoleRepository roleRepo = roleRepoFactory.create(cnx);

        List<String> current = userRepo.getRoleLibellesOfUser(userId);
        if (current != null) {
            for (String lib : current) {
                if (lib == null || lib.isBlank()) continue;
                roleRepo.findByLibelle(lib.trim()).ifPresent(r -> userRepo.removeRoleFromUser(userId, r.getId()));
            }
        }
        roleRepo.findByType(newRole).ifPresent(r -> userRepo.addRoleToUser(userId, r.getId()));
    }

    private UserSummaryDTO mapToSummary(Utilisateur user) {
        return UserSummaryDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .login(user.getLogin())
                .actif(user.isActif())
                .build();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static String buildEmailFromLogin(String login) {
        String l = (login == null) ? "user" : login.trim();
        if (l.isBlank()) l = "user";
        return l + "@dentalcenter.local";
    }
}
