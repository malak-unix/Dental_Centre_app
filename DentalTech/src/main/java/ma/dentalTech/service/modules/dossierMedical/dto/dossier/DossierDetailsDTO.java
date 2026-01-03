package ma.dentalTech.service.modules.dossierMedical.dto.dossier;

import ma.dentalTech.service.modules.dossierMedical.dto.certificat.CertificatDTO;
import ma.dentalTech.service.modules.dossierMedical.dto.consultation.ConsultationDTO;
import ma.dentalTech.service.modules.dossierMedical.dto.document.DocumentMedicalDTO;
import ma.dentalTech.service.modules.dossierMedical.dto.ordonnance.OrdonnanceDTO;
import ma.dentalTech.service.modules.dossierMedical.dto.readonly.FactureDTO;
import ma.dentalTech.service.modules.dossierMedical.dto.readonly.SituationFinanciereDTO;

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
