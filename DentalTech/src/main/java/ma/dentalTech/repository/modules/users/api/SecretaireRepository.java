package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.secretaire.Secretaire;
import ma.dentalTech.repository.common.CrudRepository;

public interface SecretaireRepository extends CrudRepository<Secretaire, Long> {
    Secretaire findByNumCNSS(String numCNSS);
}