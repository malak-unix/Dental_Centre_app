package ma.dentalTech.service.modules.listeAttente.impl;

import ma.dentalTech.entities.listeDattente.ListeAttente;
import ma.dentalTech.repository.modules.listeAttente.api.ListeAttenteRepository;
import ma.dentalTech.service.modules.listeAttente.api.ListeAttenteService;

import java.util.List;

public class ListeAttenteServiceImpl implements ListeAttenteService {

    private final ListeAttenteRepository listeRepo;

    public ListeAttenteServiceImpl(ListeAttenteRepository listeRepo) {
        this.listeRepo = listeRepo;
    }

    @Override public List<ListeAttente> getAll() { return listeRepo.findAll(); }
    @Override public ListeAttente getById(Long id) { return listeRepo.findById(id); }

    @Override
    public void create(ListeAttente l) {
        if (l == null) throw new IllegalArgumentException("ListeAttente null");
        if (l.getNomListe() == null || l.getNomListe().isBlank()) throw new IllegalArgumentException("nomListe obligatoire");
        listeRepo.create(l);
    }

    @Override
    public void update(ListeAttente l) {
        if (l == null || l.getId() == null) throw new IllegalArgumentException("ListeAttente id obligatoire");
        listeRepo.update(l);
    }

    @Override public void delete(ListeAttente l) { listeRepo.delete(l); }
    @Override public void deleteById(Long id) { listeRepo.deleteById(id); }

    @Override
    public List<ListeAttente> searchByNomListe(String nomListe) {
        return listeRepo.findByNomListe(nomListe);
    }
}
