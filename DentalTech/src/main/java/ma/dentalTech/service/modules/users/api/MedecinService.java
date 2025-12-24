package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.medecin.Medecin;
import java.util.List;

public interface MedecinService {
    List<Medecin> getAllMedecins();
    Medecin getMedecinParId(Long id);
    void recruterMedecin(Medecin medecin);
    void supprimerMedecin(Long id);
    List<Medecin> getMedecinsParSpecialite(String specialite);
}