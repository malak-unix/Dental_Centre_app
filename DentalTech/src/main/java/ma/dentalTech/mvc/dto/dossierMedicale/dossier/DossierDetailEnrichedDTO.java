package ma.dentalTech.mvc.dto.dossierMedicale.dossier;

import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.document.DocumentMedicalDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.readonly.FactureDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.readonly.SituationFinanciereDTO;

import java.util.List;

/**
 * DTO enrichi pour les détails d'un dossier médical.
 * Inclut les informations du patient et toutes les données associées.
 */
public record DossierDetailEnrichedDTO(
        DossierDTO dossier,
        // Informations patient
        Long patientId,
        String patientNomComplet,
        String patientTelephone,
        String patientEmail,
        Integer patientAge,
        String patientGroupeSanguin, // Si disponible
        // Données du dossier
        List<ConsultationDTO> consultations,
        List<OrdonnanceDTO> ordonnances,
        List<CertificatDTO> certificats,
        List<DocumentMedicalDTO> documents,
        List<FactureDTO> factures,
        SituationFinanciereDTO situationFinanciere,
        // Antécédents du patient
        List<AntecedentDTO> antecedents
) {}
