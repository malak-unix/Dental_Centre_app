package ma.dentalTech.mvc.dto.dossierMedicale.certificat;

import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;

public record SaveCertificatRequestDTO(CertificatDTO certificat, ActorDTO actor) {}
