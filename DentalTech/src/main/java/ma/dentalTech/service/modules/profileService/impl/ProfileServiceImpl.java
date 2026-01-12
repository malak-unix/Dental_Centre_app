package ma.dentalTech.service.modules.profileService.impl;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.common.utilitaire.Transaction;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Admin;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Secretaire;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.entities.users.Utilisateur;

import ma.dentalTech.mvc.dto.auth.ChangePasswordRequest;
import ma.dentalTech.mvc.dto.auth.ChangePasswordResult;
import ma.dentalTech.mvc.dto.auth.ProfileData;
import ma.dentalTech.mvc.dto.auth.ProfileUpdateRequest;
import ma.dentalTech.mvc.dto.auth.ProfileUpdateResult;

import ma.dentalTech.repository.modules.users.api.AdminRepository;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;
import ma.dentalTech.repository.modules.users.api.StaffRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.profileService.api.ChangePasswordValidator;
import ma.dentalTech.service.modules.profileService.api.ProfileService;
import ma.dentalTech.service.modules.profileService.api.ProfileValidator;

@Data
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final RepoFactory<UtilisateurRepository> userRepoFactory;
    private final RepoFactory<StaffRepository> staffRepoFactory;
    private final RepoFactory<MedecinRepository> medecinRepoFactory;
    private final RepoFactory<SecretaireRepository> secretaireRepoFactory;
    private final RepoFactory<AdminRepository> adminRepoFactory;
    private final RepoFactory<RoleRepository> roleRepoFactory;

    private final ProfileValidator validator;
    private final ChangePasswordValidator changePasswordValidator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProfileData loadByUserId(Long userId) {
        if (userId == null) return null;

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = userRepoFactory.create(cnx);
            RoleRepository roleRepo = roleRepoFactory.create(cnx);

            // ✅ FIX: getType() -> getLibelle()
            LibelleRole rolePrincipal = roleRepo.findRolesByUtilisateurId(userId)
                    .stream()
                    .map(Role::getLibelle)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (rolePrincipal == LibelleRole.MEDECIN) {
                MedecinRepository medRepo = medecinRepoFactory.create(cnx);
                Medecin m = medRepo.findById(userId);
                return (m == null) ? null : map(m, rolePrincipal);
            }

            if (rolePrincipal == LibelleRole.SECRETAIRE) {
                SecretaireRepository secRepo = secretaireRepoFactory.create(cnx);
                Secretaire s = secRepo.findById(userId);
                return (s == null) ? null : map(s, rolePrincipal);
            }

            if (rolePrincipal == LibelleRole.ADMIN) {
                AdminRepository adminRepo = adminRepoFactory.create(cnx);
                Admin a = adminRepo.findById(userId);
                return (a == null) ? null : map(a, rolePrincipal);
            }

            Utilisateur u = userRepo.findById(userId);
            return (u == null) ? null : map(u, rolePrincipal);
        });
    }

    @Override
    public ProfileUpdateResult update(ProfileUpdateRequest req) {
        Map<String, String> errors = validator.validate(req);
        if (!errors.isEmpty()) return ProfileUpdateResult.failure("Formulaire invalide", errors);

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = userRepoFactory.create(cnx);
            StaffRepository staffRepo = staffRepoFactory.create(cnx);
            MedecinRepository medRepo = medecinRepoFactory.create(cnx);
            SecretaireRepository secRepo = secretaireRepoFactory.create(cnx);
            AdminRepository adminRepo = adminRepoFactory.create(cnx);
            RoleRepository roleRepo = roleRepoFactory.create(cnx);

            // ✅ FIX: getType() -> getLibelle()
            LibelleRole rolePrincipal = roleRepo.findRolesByUtilisateurId(req.id())
                    .stream()
                    .map(Role::getLibelle)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            Map<String, String> uniqErrors = validateUniqueness(userRepo, req);
            if (!uniqErrors.isEmpty()) return ProfileUpdateResult.failure("Conflit d'unicité", uniqErrors);

            Utilisateur u = userRepo.findById(req.id());
            if (u == null) {
                return ProfileUpdateResult.failure(
                        "Utilisateur introuvable",
                        Map.of("_global", "Utilisateur introuvable.")
                );
            }

            applyUserFields(u, req);
            userRepo.update(u);

            if (rolePrincipal == LibelleRole.MEDECIN) {
                Medecin m = medRepo.findById(req.id());
                applyStaffFields(m, req);
                applyMedecinFields(m, req);
                staffRepo.updateStaffFields(m);
                medRepo.updateMedecinFields(m);
            } else if (rolePrincipal == LibelleRole.SECRETAIRE) {
                Secretaire s = secRepo.findById(req.id());
                applyStaffFields(s, req);
                applySecretaireFields(s, req);
                staffRepo.updateStaffFields(s);
                secRepo.updateSecretaireFields(s);
            } else if (rolePrincipal == LibelleRole.ADMIN) {
                Admin a = adminRepo.findById(req.id());
                applyStaffFields(a, req);
                staffRepo.updateStaffFields(a);
            }

            return ProfileUpdateResult.success("Profil mis à jour", loadByUserId(req.id()));
        });
    }

    @Override
    public ChangePasswordResult changePassword(ChangePasswordRequest req) {
        var errors = changePasswordValidator.validate(req);
        if (!errors.isEmpty()) return ChangePasswordResult.failure("Formulaire invalide", errors);

        return Transaction.initTransaction(cnx -> {
            UtilisateurRepository userRepo = userRepoFactory.create(cnx);
            Utilisateur u = userRepo.findById(req.userId());
            if (u == null) {
                return ChangePasswordResult.failure("Utilisateur introuvable.",
                        Map.of("_global", "Utilisateur introuvable."));
            }

            if (!passwordEncoder.matches(req.currentPassword(), u.getMotDePasse())) {
                return ChangePasswordResult.failure("Mot de passe actuel incorrect.",
                        Map.of("currentPassword", "Mot de passe actuel incorrect."));
            }

            userRepo.updatePassword(req.userId(), passwordEncoder.encode(req.newPassword()));
            return ChangePasswordResult.success();
        });
    }

    private String trim(String s) { return s == null ? null : s.trim(); }

    private void applyUserFields(Utilisateur u, ProfileUpdateRequest req) {
        u.setPrenom(trim(req.prenom()));
        u.setNom(trim(req.nom()));
        u.setEmail(trim(req.email()));
        u.setAdresse(trim(req.adresse()));
        u.setCin(trim(req.cin()));
        u.setTel(trim(req.tel()));
        u.setSexe(req.sexe());
        u.setDateNaissance(req.dateNaissance());
    }

    private void applyStaffFields(Staff s, ProfileUpdateRequest req) {
        s.setSalaire(req.salaire());
        s.setPrime(req.prime());
        s.setDateRecrutement(req.dateRecrutement());
        s.setSoldeConge(req.soldeConge());
    }

    private void applyMedecinFields(Medecin m, ProfileUpdateRequest req) {
        m.setSpecialite(trim(req.specialite()));
    }

    private void applySecretaireFields(Secretaire s, ProfileUpdateRequest req) {
        s.setNumCNSS(trim(req.numCNSS()));
        s.setCommission(req.commission());
    }

    private ProfileData map(Utilisateur u, LibelleRole rolePrincipal) {
        return ProfileData.builder()
                .id(u.getId()).rolePrincipal(rolePrincipal)
                .prenom(u.getPrenom()).nom(u.getNom()).email(u.getEmail())
                .adresse(u.getAdresse()).cin(u.getCin()).tel(u.getTel())
                .sexe(u.getSexe()).login(u.getLogin())
                .lastLoginDate(u.getLastLoginDate()).dateNaissance(u.getDateNaissance())
                .build();
    }

    private ProfileData map(Staff s, LibelleRole rolePrincipal) {
        ProfileData base = map((Utilisateur) s, rolePrincipal);
        return ProfileData.builder()
                .id(base.id()).rolePrincipal(base.rolePrincipal())
                .prenom(base.prenom()).nom(base.nom()).email(base.email())
                .adresse(base.adresse()).cin(base.cin()).tel(base.tel())
                .sexe(base.sexe()).login(base.login())
                .lastLoginDate(base.lastLoginDate()).dateNaissance(base.dateNaissance())
                .salaire(s.getSalaire()).prime(s.getPrime())
                .dateRecrutement(s.getDateRecrutement()).soldeConge(s.getSoldeConge())
                .build();
    }

    private ProfileData map(Medecin m, LibelleRole rolePrincipal) {
        ProfileData base = map((Staff) m, rolePrincipal);
        return ProfileData.builder()
                .id(base.id()).rolePrincipal(base.rolePrincipal())
                .prenom(base.prenom()).nom(base.nom()).email(base.email())
                .adresse(base.adresse()).cin(base.cin()).tel(base.tel())
                .sexe(base.sexe()).login(base.login())
                .lastLoginDate(base.lastLoginDate()).dateNaissance(base.dateNaissance())
                .salaire(base.salaire()).prime(base.prime())
                .dateRecrutement(base.dateRecrutement()).soldeConge(base.soldeConge())
                .specialite(m.getSpecialite())
                .build();
    }

    private ProfileData map(Secretaire s, LibelleRole rolePrincipal) {
        ProfileData base = map((Staff) s, rolePrincipal);
        return ProfileData.builder()
                .id(base.id()).rolePrincipal(base.rolePrincipal())
                .prenom(base.prenom()).nom(base.nom()).email(base.email())
                .adresse(base.adresse()).cin(base.cin()).tel(base.tel())
                .sexe(base.sexe()).login(base.login())
                .lastLoginDate(base.lastLoginDate()).dateNaissance(base.dateNaissance())
                .salaire(base.salaire()).prime(base.prime())
                .dateRecrutement(base.dateRecrutement()).soldeConge(base.soldeConge())
                .numCNSS(s.getNumCNSS())
                .commission(s.getCommission())
                .build();
    }

    private Map<String, String> validateUniqueness(UtilisateurRepository userRepo, ProfileUpdateRequest req) {
        Map<String, String> e = new LinkedHashMap<>();
        String email = trim(req.email());
        if (email != null) {
            Utilisateur byEmail = userRepo.findByEmail(email).orElse(null);
            if (byEmail != null && !byEmail.getId().equals(req.id())) {
                e.put("email", "Email déjà utilisé.");
            }
        }
        return e;
    }
}
