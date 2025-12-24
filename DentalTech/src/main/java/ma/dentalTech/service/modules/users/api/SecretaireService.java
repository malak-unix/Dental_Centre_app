package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.secretaire.Secretaire;
import java.util.List;

public interface SecretaireService {
    List<Secretaire> getAllSecretaires();
    Secretaire getSecretaireParId(Long id);
    void recruterSecretaire(Secretaire secretaire);
    void supprimerSecretaire(Long id);
}