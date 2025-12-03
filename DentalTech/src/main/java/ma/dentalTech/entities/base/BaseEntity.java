package ma.dentalTech.entities.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    protected Long id;
    protected LocalDate dateCreation;
    protected LocalDateTime dateDerniereModification;
    protected String modifiePar;
    protected String creePar;
}
