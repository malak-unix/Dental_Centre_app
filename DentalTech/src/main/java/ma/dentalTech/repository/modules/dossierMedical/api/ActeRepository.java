package ma.dentalTech.repository.modules.dossierMedical.api;
import java.time.LocalDateTime;

public interface ActeRepository {
   //Methodes ajouté par Aya BERDAY - kan st3mlhom f dashboard
    Integer countActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);
    Double sumMontantActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);

}
