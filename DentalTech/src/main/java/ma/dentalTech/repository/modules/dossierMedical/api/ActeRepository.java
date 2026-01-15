package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.common.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ActeRepository extends CrudRepository<Acte, Long> {
    List<Acte> findByCategorie(String categorie);
    List<Acte> searchByLibelle(String keyword);
    boolean existsById(Long id);
    long count();
    long countAll();
    long countActesDuJourPourMedecin(Long medecinId);
    BigDecimal totalRecetteDuJourPourMedecin(Long medecinId);

    List<Acte> findPage(int limit, int offset);
    Integer countActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);
    Double sumMontantActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);
}
