package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.controllers.modules.users.impl.UserManagementControllerImpl;
import ma.dentalTech.mvc.dto.users.*;
import ma.dentalTech.mvc.ui.modules.users.UserManagementFrame;
import ma.dentalTech.service.modules.users.api.UserManagementService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class TestUserManagementUiNoDb {

    public static void main(String[] args) {

        // ✅ Données en mémoire (comme si c’était la DB)
        List<UserSummaryDTO> memory = new ArrayList<>();
        memory.add(UserSummaryDTO.builder().id(1L).nom("Admin").prenom("Tech").login("admin").actif(true).build());
        memory.add(UserSummaryDTO.builder().id(2L).nom("Achari").prenom("Malak").login("malak").actif(true).build());
        memory.add(UserSummaryDTO.builder().id(3L).nom("Berday").prenom("Aya").login("aya").actif(true).build());

        AtomicLong seq = new AtomicLong(10);

        UserManagementService fakeService = new UserManagementService() {

            @Override
            public UserSummaryDTO createAdmin(CreateAdminRequestDTO request) {
                UserSummaryDTO u = UserSummaryDTO.builder()
                        .id(seq.incrementAndGet())
                        .nom(request.getNom())
                        .prenom(request.getPrenom())
                        .login(request.getLogin())
                        .actif(true)
                        .build();
                memory.add(u);
                return u;
            }

            @Override
            public UserSummaryDTO createMedecin(CreateMedecinRequestDTO request) {
                UserSummaryDTO u = UserSummaryDTO.builder()
                        .id(seq.incrementAndGet())
                        .nom(request.getNom())
                        .prenom(request.getPrenom())
                        .login(request.getLogin())
                        .actif(true)
                        .build();
                memory.add(u);
                return u;
            }

            @Override
            public UserSummaryDTO createSecretaire(CreateSecretaireRequestDTO request) {
                UserSummaryDTO u = UserSummaryDTO.builder()
                        .id(seq.incrementAndGet())
                        .nom(request.getNom())
                        .prenom(request.getPrenom())
                        .login(request.getLogin())
                        .actif(true)
                        .build();
                memory.add(u);
                return u;
            }

            @Override
            public UserSummaryDTO getUserById(Long id) {
                return memory.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
            }

            @Override
            public List<UserSummaryDTO> getAllUsers() {
                return new ArrayList<>(memory);
            }

            @Override
            public List<UserSummaryDTO> searchUsersByKeyword(String keyword) {
                if (keyword == null || keyword.isBlank()) return getAllUsers();
                String k = keyword.toLowerCase().trim();
                return memory.stream().filter(u ->
                        (u.getNom() != null && u.getNom().toLowerCase().contains(k)) ||
                                (u.getPrenom() != null && u.getPrenom().toLowerCase().contains(k)) ||
                                (u.getLogin() != null && u.getLogin().toLowerCase().contains(k))
                ).toList();
            }

            @Override
            public UserSummaryDTO updateUserProfile(Long id, UserSaveRequestDTO request) {
                UserSummaryDTO u = getUserById(id);
                if (u == null) return null;

                // ✅ simulate update (nom/prenom/login)
                if (request.getNom() != null && !request.getNom().isBlank()) u.setNom(request.getNom().trim());
                if (request.getPrenom() != null && !request.getPrenom().isBlank()) u.setPrenom(request.getPrenom().trim());
                if (request.getLogin() != null && !request.getLogin().isBlank()) u.setLogin(request.getLogin().trim());

                // password/role ignorés ici (UI demo)
                return u;
            }

            @Override public void assignRoleToUser(Long utilisateurId, LibelleRole roleType) {}
            @Override public void removeRoleFromUser(Long utilisateurId, LibelleRole roleType) {}
        };

        UserManagementController ctrl = new UserManagementControllerImpl(fakeService);

        SwingUtilities.invokeLater(() -> {
            UserManagementFrame f = new UserManagementFrame(ctrl);
            f.setVisible(true);
        });
    }
}
