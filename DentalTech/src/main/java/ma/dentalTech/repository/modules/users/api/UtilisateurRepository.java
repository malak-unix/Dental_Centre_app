package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.common.exceptions.DaoException;

import java.time.LocalDate;

public interface UtilisateurRepository {

    // Pour dashboard unique : rôle de l'utilisateur
    String findRoleByUtilisateurId(Long utilisateurId) throws DaoException;

    // Ces méthodes sont déjà utilisées dans mon DashboardServiceImpl-AYA BERDAY
    Integer countAll() throws DaoException;
    Integer countByRole(String role) throws DaoException;
    Integer countConnexionsJour(LocalDate date) throws DaoException;
}
