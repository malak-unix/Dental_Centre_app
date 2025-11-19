package ma.dentalTech.mvc.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDTO {

    private String nomComplet;
    private int age;
    private String dateCreationFormatee;

    // Lombok génère automatiquement :
    // - public static PatientDTOBuilder builder()
    // - public String getNomComplet()
    // - public void setNomComplet(String nomComplet)
    // - public String toString()
    // - public boolean equals(Object other)
    // - public int hashCode()

    // Toutes les implémentations manuelles ont été supprimées.
}