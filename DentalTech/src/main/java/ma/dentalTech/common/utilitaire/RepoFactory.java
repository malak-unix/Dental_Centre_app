package ma.dentalTech.common.utilitaire;

import java.sql.Connection;

/**
 * Interface pour la création de repositories avec injection de connexion.
 */
@FunctionalInterface
public interface RepoFactory<T> {
    T create(Connection connection);
}