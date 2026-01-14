package ma.dentalTech.service.modules.agenda.impl;

import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.repository.modules.agenda.api.ListeAttenteRepository;
import ma.dentalTech.service.modules.agenda.api.ListeAttenteService;
import ma.dentalTech.service.modules.agenda.api.RdvService;

import java.util.List;

public class ListeAttenteServiceImpl implements ListeAttenteService {

    private final ListeAttenteRepository listeRepo;
    private final RdvService rdvService; // ✅ AJOUT

    // ✅ Nouveau constructeur
    public ListeAttenteServiceImpl(ListeAttenteRepository listeRepo, RdvService rdvService) {
        this.listeRepo = listeRepo;
        this.rdvService = rdvService;
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
        listeRepo.create(l);
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
        return listeRepo.findByNomListe(nomListe);
    }

    // ✅ AJOUT : créer un RDV depuis une liste d’attente
    @Override
    public void programmer(Long idListeAttente, RDV rdv) {
        if (idListeAttente == null || idListeAttente <= 0)
            throw new IllegalArgumentException("idListeAttente obligatoire");

        ListeAttente la = listeRepo.findById(idListeAttente);
        if (la == null)
            throw new IllegalArgumentException("Liste d'attente introuvable (id=" + idListeAttente + ")");

        if (rdv == null) throw new IllegalArgumentException("RDV null");
        rdv.setListeAttenteId(idListeAttente); // ✅ forcer

        if (rdvService == null)
            throw new IllegalStateException("RdvService introuvable (bean rdv.service)");

        rdvService.create(rdv); // ✅ ça applique tes validations + insert repo
    }
}
