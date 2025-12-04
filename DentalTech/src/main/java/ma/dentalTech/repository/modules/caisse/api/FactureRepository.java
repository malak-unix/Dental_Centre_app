package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FactureRepository extends CrudRepository<Facture, Long> {


    List<Facture> findByStatut(StatutFacture statut);
    List<Facture> findByDateFactureBetween(LocalDateTime start, LocalDateTime end);

    Double calculateTotalRevenue(LocalDateTime start, LocalDateTime end);
    Double calculateTotalUnpaid();
}