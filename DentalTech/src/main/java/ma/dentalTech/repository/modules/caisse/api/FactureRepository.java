package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.repository.common.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FactureRepository extends CrudRepository<Facture, Long> {

    List<Facture> findByDateBetween(LocalDateTime start, LocalDateTime end);

    Double calculateTotalFactures(LocalDateTime start, LocalDateTime end);

    Double calculateTotalRegle(LocalDateTime start, LocalDateTime end);

    Double calculateTotalNonRegle(LocalDateTime start, LocalDateTime end);

    BigDecimal totalRecetteDuJour();

    // === AJOUTS dossier médical / consultation ===
    Facture findByConsultationId(Long consultationId);

    boolean existsByConsultationId(Long consultationId);

    List<Facture> findByDossierId(Long dossierId);

    List<Facture> findPage(int limit, int offset);
    long count();

    boolean updatePayment(Long factureId, double totalPaye, StatutFacture statut, String modifiePar);

}
