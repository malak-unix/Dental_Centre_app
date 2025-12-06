package ma.dentalTech.entities.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    protected Long id;
    protected LocalDateTime dateCreation;
    protected LocalDateTime dateDerniereModification;
    protected String creePar;
    protected String modifiePar;
}
