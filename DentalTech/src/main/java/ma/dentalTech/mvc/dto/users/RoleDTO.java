package ma.dentalTech.mvc.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.enums.LibelleRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long id;
    private LibelleRole libelle;
    private String privileges; // Comma separated string: "READ_PATIENT,EDIT_PATIENT"
}
