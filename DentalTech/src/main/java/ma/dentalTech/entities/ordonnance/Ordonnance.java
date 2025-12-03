package ma.dentalTech.entities.ordonnance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ordonnance extends BaseEntity {

    private Long idOrd;
    private LocalDate date;
}
