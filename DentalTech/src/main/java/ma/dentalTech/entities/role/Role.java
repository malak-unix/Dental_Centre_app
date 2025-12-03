package ma.dentalTech.entities.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.LibelleRole;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends BaseEntity {

    private Long idRole;
    private LibelleRole libelle;     // Enum
    private List<String> privilege;
}
