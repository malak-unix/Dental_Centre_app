package ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere;

import ma.dentalTech.entities.enums.StatutSituationFinanciere;

import java.util.List;

/**
 * DTO pour afficher les détails complets d'une situation financière.
 * Inclut les factures avec leurs consultations associées.
 */
public record SituationFinanciereDetailDTO(
        Long id,
        Long dossierId,
        Long patientId,
        String patientNomComplet,
        Long medecinId,
        Double totalDesActes,
        Double totalPaye,
        Double credit,
        Double solde, // totalDesActes - totalPaye + credit
        StatutSituationFinanciere statut,
        List<FactureDetailDTO> factures // Factures avec consultations associées
) {}
