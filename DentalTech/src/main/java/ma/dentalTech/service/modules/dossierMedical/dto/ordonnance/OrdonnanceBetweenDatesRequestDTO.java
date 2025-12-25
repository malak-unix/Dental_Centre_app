package ma.dentalTech.service.modules.dossierMedical.dto.ordonnance;

import java.time.LocalDate;

public record OrdonnanceBetweenDatesRequestDTO(LocalDate start, LocalDate end) {}
