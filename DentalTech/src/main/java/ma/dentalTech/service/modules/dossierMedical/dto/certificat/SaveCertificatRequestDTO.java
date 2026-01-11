package ma.dentalTech.service.modules.dossierMedical.dto.certificat;

import ma.dentalTech.service.modules.dossierMedical.dto.common.ActorDTO;

public record SaveCertificatRequestDTO(CertificatDTO certificat, ActorDTO actor) {}
