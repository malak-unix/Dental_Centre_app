package ma.dentalTech.entities.medecin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.staff.Staff;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Medecin extends Staff {

    private String specialite;
    private AgendaMensuel agendaMensuel;
}
