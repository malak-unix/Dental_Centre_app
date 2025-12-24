package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.*;
import ma.dentalTech.mvc.dto.users.*;
import ma.dentalTech.repository.modules.users.api.*;
import ma.dentalTech.service.modules.users.api.UserManagementService;
import java.util.ArrayList;
import java.util.List;

public class UserManagementServiceImpl implements UserManagementService {

    private final UtilisateurRepository utilisateurRepository;
    private final MedecinRepository medecinRepository;
    private final SecretaireRepository secretaireRepository;

    public UserManagementServiceImpl(UtilisateurRepository utilisateurRepository,
                                     MedecinRepository medecinRepository,
                                     SecretaireRepository secretaireRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.medecinRepository = medecinRepository;
        this.secretaireRepository = secretaireRepository;
    }

    @Override
    public UserSummaryDTO createAdmin(CreateAdminRequestDTO request) {
        Admin admin = new Admin();
        admin.setNom(request.getNom());
        admin.setPrenom(request.getPrenom());
        admin.setLogin(request.getLogin());
        admin.setMotDePasse(request.getPassword());
        admin.setActif(true);
        utilisateurRepository.create(admin); // Retourne void donc on ne fait pas de return ici
        return mapToSummary(admin);
    }

    @Override
    public UserSummaryDTO createMedecin(CreateMedecinRequestDTO request) {
        Medecin m = new Medecin();
        m.setNom(request.getNom());
        m.setPrenom(request.getPrenom());
        m.setLogin(request.getLogin());
        m.setMotDePasse(request.getPassword());
        m.setSpecialite(request.getSpecialite());
        m.setActif(true);
        medecinRepository.create(m);
        return mapToSummary(m);
    }

    @Override
    public UserSummaryDTO createSecretaire(CreateSecretaireRequestDTO request) {
        Secretaire s = new Secretaire();
        s.setNom(request.getNom());
        s.setPrenom(request.getPrenom());
        s.setLogin(request.getLogin());
        s.setMotDePasse(request.getPassword());
        s.setNumCNSS(request.getNumCNSS());
        s.setActif(true);
        secretaireRepository.create(s);
        return mapToSummary(s);
    }

    @Override
    public List<UserSummaryDTO> getAllUsers() {
        List<Utilisateur> users = utilisateurRepository.findAll();
        List<UserSummaryDTO> dtos = new ArrayList<>();
        for (Utilisateur u : users) {
            dtos.add(mapToSummary(u));
        }
        return dtos;
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

    @Override
    public UserSummaryDTO getUserById(Long id) {
        Utilisateur u = utilisateurRepository.findById(id);
        return (u != null) ? mapToSummary(u) : null;
    }

    @Override public List<UserSummaryDTO> searchUsersByKeyword(String keyword) { return new ArrayList<>(); }
    @Override public UserSummaryDTO updateUserProfile(Long id, UserSaveRequestDTO request) { return null; }
    @Override public void assignRoleToUser(Long utilisateurId, LibelleRole roleType) {}
    @Override public void removeRoleFromUser(Long utilisateurId, LibelleRole roleType) {}
}