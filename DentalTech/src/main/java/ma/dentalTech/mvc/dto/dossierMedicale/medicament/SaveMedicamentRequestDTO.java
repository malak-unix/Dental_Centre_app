package ma.dentalTech.mvc.dto.dossierMedicale.medicament;

import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;

public record SaveMedicamentRequestDTO(MedicamentDTO medicament, ActorDTO actor) {}
