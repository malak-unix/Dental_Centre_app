package ma.dentalTech.mvc.dto.patient;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientListDto {
    private Long id;
    private String nomComplet;
    private String telephone;
}
