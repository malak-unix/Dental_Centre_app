package ma.dentalTech.mvc.controllers.modules.users.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.dto.users.*;
import ma.dentalTech.service.modules.users.api.UserManagementService;

import java.util.List;

@RequiredArgsConstructor
public class UserManagementControllerImpl implements UserManagementController {

    private final UserManagementService userManagementService;

    @Override
    public UserSummaryDTO createAdmin(CreateAdminRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("createAdmin: request null");
        return userManagementService.createAdmin(request);
    }

    @Override
    public UserSummaryDTO createMedecin(CreateMedecinRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("createMedecin: request null");
        return userManagementService.createMedecin(request);
    }

    @Override
    public UserSummaryDTO createSecretaire(CreateSecretaireRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("createSecretaire: request null");
        return userManagementService.createSecretaire(request);
    }

    @Override
    public UserSummaryDTO getUserById(Long id) {
        if (id == null) throw new IllegalArgumentException("getUserById: id null");
        return userManagementService.getUserById(id);
    }

    @Override
    public List<UserSummaryDTO> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    @Override
    public List<UserSummaryDTO> searchUsersByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        return userManagementService.searchUsersByKeyword(keyword);
    }

    @Override
    public UserSummaryDTO updateUserProfile(Long id, UserSaveRequestDTO request) {
        if (id == null) throw new IllegalArgumentException("updateUserProfile: id null");
        if (request == null) throw new IllegalArgumentException("updateUserProfile: request null");
        return userManagementService.updateUserProfile(id, request);
    }

    @Override
    public void assignRoleToUser(Long utilisateurId, LibelleRole roleType) {
        if (utilisateurId == null) throw new IllegalArgumentException("assignRoleToUser: utilisateurId null");
        if (roleType == null) throw new IllegalArgumentException("assignRoleToUser: roleType null");
        userManagementService.assignRoleToUser(utilisateurId, roleType);
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, LibelleRole roleType) {
        if (utilisateurId == null) throw new IllegalArgumentException("removeRoleFromUser: utilisateurId null");
        if (roleType == null) throw new IllegalArgumentException("removeRoleFromUser: roleType null");
        userManagementService.removeRoleFromUser(utilisateurId, roleType);
    }
}
