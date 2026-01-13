package ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere;

import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDate;

/**
 * DTO pour afficher une facture dans les détails d'une situation financière.
 * Inclut les informations de la consultation associée.
 */
public record FactureDetailDTO(
        Long factureId,
        String numeroFacture, // Ex: "F0099"
        LocalDate dateFacture,
        Double totalFacture,
        Double totalPaye,
        Double reste,
        StatutFacture statut,
        Long consultationId,
        String consultationLibelle // Ex: "Consultation #123 - TERMINE"
) {}
