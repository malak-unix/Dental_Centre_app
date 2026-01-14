package ma.dentalTech.service.modules.agenda.api;

import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.entities.agenda.RDV;

import java.util.List;

public interface ListeAttenteService {
    List<ListeAttente> getAll();
    ListeAttente getById(Long id);
    void create(ListeAttente l);
    void update(ListeAttente l);
    void delete(ListeAttente l);
    void deleteById(Long id);

    List<ListeAttente> searchByNomListe(String nomListe);

    // ✅ AJOUT
    void programmer(Long idListeAttente, RDV rdv);
}
