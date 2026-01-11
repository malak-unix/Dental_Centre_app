package ma.dentalTech.service.modules.dossierMedical.dto.document;

import ma.dentalTech.service.modules.dossierMedical.dto.common.ActorDTO;

public record SaveDocumentMedicalRequestDTO(DocumentMedicalDTO document, ActorDTO actor) {}
