package ma.dentalTech.repository.modules.agenda.api;

import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface AgendaMensuelRepository extends CrudRepository<AgendaMensuel, Long> {

    AgendaMensuel findByMedecinAndMonth(Long medecinId, String mois, int annee);

    List<AgendaMensuel> findByMedecin(Long medecinId);
}
