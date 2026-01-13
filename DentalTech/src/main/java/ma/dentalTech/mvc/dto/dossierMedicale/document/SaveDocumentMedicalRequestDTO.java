package ma.dentalTech.mvc.dto.dossierMedicale.document;

import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;

public record SaveDocumentMedicalRequestDTO(DocumentMedicalDTO document, ActorDTO actor) {}
