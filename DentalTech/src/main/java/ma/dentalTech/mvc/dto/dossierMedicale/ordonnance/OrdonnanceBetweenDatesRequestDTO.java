package ma.dentalTech.mvc.dto.dossierMedicale.ordonnance;

import java.time.LocalDate;

public record OrdonnanceBetweenDatesRequestDTO(LocalDate start, LocalDate end) {}
