package ma.dentalTech.entities.medecin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.staff.Staff;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medecin extends Staff {

    private String specialite;
    private AgendaMensuel agendaMensuel;
}
