package ma.dentalTech.entities.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.LibelleRole;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Role extends BaseEntity {

    private LibelleRole libelle;     // Enum
    private List<String> privilege;
}
