package ma.dentalTech.service.modules.dossierMedical.dto.medicament;

import ma.dentalTech.service.modules.dossierMedical.dto.common.ActorDTO;

public record SaveMedicamentRequestDTO(MedicamentDTO medicament, ActorDTO actor) {}
