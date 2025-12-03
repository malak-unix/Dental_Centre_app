package ma.dentalTech.entities.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public abstract class BaseEntity {

    protected Long idEntite;
    protected LocalDate dateCreation;
    protected LocalDateTime dateDerniereModification;
    protected String modifiePar;
    protected String creePar;
}
