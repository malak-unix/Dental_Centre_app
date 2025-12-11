package ma.dentalTech.service.modules.ordonnance.api;

import ma.dentalTech.entities.ordonnance.Ordonnance;

import java.time.LocalDate;
import java.util.List;

public interface OrdonnanceService {

    Ordonnance creerOrdonnance(Long dossierId, Long consultationId, LocalDate date, String utilisateur);

    Ordonnance getById(Long id);

    List<Ordonnance> getByDossier(Long dossierId);

    List<Ordonnance> getByConsultation(Long consultationId);

    List<Ordonnance> getByDate(LocalDate date);

    void supprimerOrdonnance(Long id);
}
