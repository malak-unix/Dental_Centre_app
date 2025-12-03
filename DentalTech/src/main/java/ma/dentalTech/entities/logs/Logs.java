package ma.dentalTech.entities.logs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.EntiteAttribue;
import ma.dentalTech.entities.enums.TypeLog;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Logs extends BaseEntity {

    private LocalDate dateL;
    private LocalTime timeL;
    private Long idUtilisateur;
    private EntiteAttribue entiteAttribue;   // Enum
    private TypeLog actionType;             // ou TypeLog si tu veux séparer type de message
    private String action;
}
