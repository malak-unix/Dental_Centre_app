package ma.dentalTech.service.modules.listeAttente.api;

import ma.dentalTech.entities.listeDattente.ListeAttente;

import java.util.List;

public interface ListeAttenteService {
    List<ListeAttente> getAll();
    ListeAttente getById(Long id);
    void create(ListeAttente l);
    void update(ListeAttente l);
    void delete(ListeAttente l);
    void deleteById(Long id);

    List<ListeAttente> searchByNomListe(String nomListe);
}
