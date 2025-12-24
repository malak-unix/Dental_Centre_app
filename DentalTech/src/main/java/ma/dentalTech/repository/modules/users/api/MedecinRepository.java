package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.medecin.Medecin;
import ma.dentalTech.repository.common.CrudRepository;
import java.util.List;

public interface MedecinRepository extends CrudRepository<Medecin, Long> {
    // Méthode spécifique pour trouver des médecins par spécialité
    List<Medecin> findBySpecialite(String specialite);
}