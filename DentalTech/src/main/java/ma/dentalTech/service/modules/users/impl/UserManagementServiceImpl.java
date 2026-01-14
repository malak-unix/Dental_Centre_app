package ma.dentalTech.service.modules.users.impl;

import lombok.AllArgsConstructor;
import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.common.utilitaire.Transaction;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.*;
import ma.dentalTech.mvc.dto.users.*;
import ma.dentalTech.repository.modules.users.api.*;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.users.api.UserManagementService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final RepoFactory<UtilisateurRepository> utilisateurRepoFactory;
    private final RepoFactory<MedecinRepository> medecinRepoFactory;
    private final RepoFactory<SecretaireRepository> secretaireRepoFactory;
    private final RepoFactory<RoleRepository> roleRepoFactory;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserSummaryDTO createAdmin(CreateAdminRequestDTO request) {
        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository utilisateurRepository = utilisateurRepoFactory.create(cnx);

            Admin admin = new Admin();
            admin.setNom(request.getNom());
            admin.setPrenom(request.getPrenom());
            admin.setLogin(request.getLogin());
            admin.setMotDePasse(passwordEncoder.encode(request.getPassword())); // ✅ hash
            admin.setActif(true);

            utilisateurRepository.create(admin);

            // (Optionnel) attribuer rôle ADMIN si ton schéma le permet
            assignRoleInternal(cnx, admin.getId(), LibelleRole.ADMIN);

            return mapToSummary(admin);
        });
    }

    @Override
    public UserSummaryDTO createMedecin(CreateMedecinRequestDTO request) {
        return Transaction.initTransaction(cnx -> {
            MedecinRepository medecinRepository = medecinRepoFactory.create(cnx);

            Medecin m = new Medecin();
            m.setNom(request.getNom());
            m.setPrenom(request.getPrenom());
            m.setLogin(request.getLogin());
            m.setMotDePasse(passwordEncoder.encode(request.getPassword())); // ✅ hash
            m.setSpecialite(request.getSpecialite());
            m.setActif(true);

            medecinRepository.create(m);

            assignRoleInternal(cnx, m.getId(), LibelleRole.MEDECIN);

            return mapToSummary(m);
        });
    }

    @Override
    public UserSummaryDTO createSecretaire(CreateSecretaireRequestDTO request) {
        return Transaction.initTransaction(cnx -> {
            SecretaireRepository secretaireRepository = secretaireRepoFactory.create(cnx);

            Secretaire s = new Secretaire();
            s.setNom(request.getNom());
            s.setPrenom(request.getPrenom());
            s.setLogin(request.getLogin());
            s.setMotDePasse(passwordEncoder.encode(request.getPassword())); // ✅ hash
            s.setNumCNSS(request.getNumCNSS());
            s.setActif(true);

            secretaireRepository.create(s);

            assignRoleInternal(cnx, s.getId(), LibelleRole.SECRETAIRE);

            return mapToSummary(s);
        });
    }

    @Override
    public UserSummaryDTO getUserById(Long id) {
        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository utilisateurRepository = utilisateurRepoFactory.create(cnx);
            Utilisateur u = utilisateurRepository.findById(id);
            return (u != null) ? mapToSummary(u) : null;
        });
    }

    @Override
    public List<UserSummaryDTO> getAllUsers() {
        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository utilisateurRepository = utilisateurRepoFactory.create(cnx);
            List<Utilisateur> users = utilisateurRepository.findAll();

            List<UserSummaryDTO> dtos = new ArrayList<>();
            if (users != null) {
                for (Utilisateur u : users) {
                    if (u != null)
                        dtos.add(mapToSummary(u));
                }
            }
            return dtos;
        });
    }

    @Override
    public List<UserSummaryDTO> searchUsersByKeyword(String keyword) {
        // ✅ avoid DB call if keyword is empty
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>();
        }

        final String kw = keyword.trim();

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository utilisateurRepository = utilisateurRepoFactory.create(cnx);
            List<Utilisateur> users = utilisateurRepository.searchByNom(kw);

            List<UserSummaryDTO> dtos = new ArrayList<>();
            if (users != null) {
                for (Utilisateur u : users) {
                    if (u != null)
                        dtos.add(mapToSummary(u));
                }
            }
            return dtos;
        });
    }

    @Override
    public UserSummaryDTO updateUserProfile(Long id, UserSaveRequestDTO request) {

        if (id == null || request == null)
            return null;

        return Transaction.initTransaction(cnx -> {

            UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);

            Utilisateur existing = userRepo.findById(id);
            if (existing == null) {
                return null;
            }

            // ✅ update champs simples si fournis
            if (request.getNom() != null && !request.getNom().isBlank()) {
                existing.setNom(request.getNom().trim());
            }
            if (request.getPrenom() != null && !request.getPrenom().isBlank()) {
                existing.setPrenom(request.getPrenom().trim());
            }
            if (request.getLogin() != null && !request.getLogin().isBlank()) {
                existing.setLogin(request.getLogin().trim());
            }

            // ✅ update user (profil)
            userRepo.update(existing);

            // ✅ mot de passe : uniquement si rempli
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                String encoded = passwordEncoder.encode(request.getPassword());
                userRepo.updatePassword(id, encoded);
            }

            // ✅ rôle : si fourni → modèle "propre" (remplacement)
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
            if (roleOpt.isPresent()) {
                userRepo.removeRoleFromUser(utilisateurId, roleOpt.get().getId());
            }
            return null;
        });
    }

    // ==========================
    // Helpers
    // ==========================
    private void assignRoleInternal(java.sql.Connection cnx, Long userId, LibelleRole roleType) {
        if (userId == null || roleType == null)
            return;

        RoleRepository roleRepo = roleRepoFactory.create(cnx);
        UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);

        Optional<Role> roleOpt = roleRepo.findByType(roleType);
        if (roleOpt.isPresent()) {
            userRepo.addRoleToUser(userId, roleOpt.get().getId());
        }
    }

    /**
     * ✅ Modèle "propre" : 1 utilisateur = 1 rôle principal
     * → on supprime les rôles actuels puis on ajoute le nouveau.
     */
    private void replaceRolesInternal(java.sql.Connection cnx, Long userId, LibelleRole newRole) {
        if (userId == null || newRole == null)
            return;

        UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);
        RoleRepository roleRepo = roleRepoFactory.create(cnx);

        // 1) supprimer les rôles actuels
        List<String> current = userRepo.getRoleLibellesOfUser(userId);
        if (current != null) {
            for (String lib : current) {
                if (lib == null || lib.isBlank())
                    continue;

                roleRepo.findByLibelle(lib.trim())
                        .ifPresent(r -> userRepo.removeRoleFromUser(userId, r.getId()));
            }
        }

        // 2) ajouter le nouveau rôle
        roleRepo.findByType(newRole)
                .ifPresent(r -> userRepo.addRoleToUser(userId, r.getId()));
    }

    @Override
    public void activateUser(Long utilisateurId) {
        setActivationStatus(utilisateurId, true);
    }

    @Override
    public void deactivateUser(Long utilisateurId) {
        setActivationStatus(utilisateurId, false);
    }

    private void setActivationStatus(Long userId, boolean status) {
        if (userId == null)
            return;
        Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = utilisateurRepoFactory.create(cnx);
            Utilisateur u = userRepo.findById(userId);
            if (u != null) {
                u.setActif(status);
                userRepo.update(u);
            }
            return null;
        });
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
}
