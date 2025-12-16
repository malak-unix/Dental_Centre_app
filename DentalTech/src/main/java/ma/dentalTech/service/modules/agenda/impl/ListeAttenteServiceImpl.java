package ma.dentalTech.service.modules.agenda.impl;

import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.repository.modules.agenda.api.ListeAttenteRepository;
import ma.dentalTech.service.modules.agenda.api.ListeAttenteService;

import java.util.List;

public class ListeAttenteServiceImpl implements ListeAttenteService {

    private final ListeAttenteRepository listeRepo;

    public ListeAttenteServiceImpl(ListeAttenteRepository listeRepo) {
        this.listeRepo = listeRepo;
    }

    @Override
    public List<ListeAttente> getAll() {
        return listeRepo.findAll();
    }

    @Override
    public ListeAttente getById(Long id) {
        return listeRepo.findById(id);
    }

    @Override
    public void create(ListeAttente l) {
        if (l == null) throw new IllegalArgumentException("ListeAttente null");
        if (l.getNom() == null || l.getNom().isBlank())
            throw new IllegalArgumentException("nom obligatoire");
        listeRepo.create(l); // ou insert(l) si votre repo utilise insert
    }

    @Override
    public void update(ListeAttente l) {
        if (l == null || l.getId() == null)
            throw new IllegalArgumentException("ListeAttente id obligatoire");
        listeRepo.update(l);
    }

    @Override
    public void delete(ListeAttente l) {
        if (l == null || l.getId() == null)
            throw new IllegalArgumentException("ListeAttente id obligatoire");
        listeRepo.delete(l);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        listeRepo.deleteById(id);
    }

    @Override
    public List<ListeAttente> searchByNomListe(String nomListe) {
        if (nomListe == null || nomListe.isBlank()) return List.of();
        // on garde le nom de méthode de l'interface, mais on mappe vers "nom"
        return listeRepo.findByNomListe(nomListe); // ou findByNom(...) si tu renommes côté repo
    }
}
