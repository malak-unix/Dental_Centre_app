package ma.dentalTech.mvc.dto.dossierMedicale.dossier;

import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.document.DocumentMedicalDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.readonly.FactureDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.readonly.SituationFinanciereDTO;

import java.util.List;

public record DossierDetailsDTO(
        DossierDTO dossier,
        List<ConsultationDTO> consultations,
        List<DocumentMedicalDTO> documents,
        List<OrdonnanceDTO> ordonnances,
        List<CertificatDTO> certificats,
        List<FactureDTO> factures,                 // lecture caisse
        SituationFinanciereDTO situationFinanciere // lecture caisse
) {}
