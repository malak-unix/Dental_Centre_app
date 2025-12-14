package ma.dentalTech.service.modules.listeAttente.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.listeDattente.ListeAttente;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.repository.modules.listeAttente.api.ListeAttenteRepository;
import ma.dentalTech.repository.modules.rdv.api.RdvRepository;
import ma.dentalTech.service.modules.listeAttente.api.ListeAttenteService;

import java.util.List;

public class ListeAttenteServiceImpl implements ListeAttenteService {

    private final ListeAttenteRepository listeAttenteRepository;
    private final RdvRepository rdvRepository;

    public ListeAttenteServiceImpl(ListeAttenteRepository listeAttenteRepository,
                                   RdvRepository rdvRepository) {
        this.listeAttenteRepository = listeAttenteRepository;
        this.rdvRepository = rdvRepository;
    }

    @Override
    public ListeAttente creerListe(String nomListe, String user) throws ServiceException {
        try {
            ListeAttente liste = ListeAttente.builder()
                    .nomListe(nomListe)
                    .creePar(user)
                    .modifiePar(user)
                    .build();

            listeAttenteRepository.create(liste);
            return liste;

        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la création de la liste d'attente", e);
        }
    }

    @Override
    public ListeAttente trouverParId(Long id) throws ServiceException {
        try {
            return listeAttenteRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche de la liste d'attente id=" + id, e);
        }
    }

    @Override
    public ListeAttente trouverParNom(String nomListe) throws ServiceException {
        try {
            return listeAttenteRepository.findByNomListe(nomListe);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche de la liste d'attente nom=" + nomListe, e);
        }
    }

    @Override
    public List<ListeAttente> listerToutes() throws ServiceException {
        try {
            return listeAttenteRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du chargement des listes d'attente", e);
        }
    }

    @Override
    public void ajouterRdvAListe(Long listeAttenteId, Long rdvId, String user) throws ServiceException {
        try {
            ListeAttente liste = listeAttenteRepository.findById(listeAttenteId);
            if (liste == null) {
                throw new ServiceException("Aucune liste d'attente trouvée avec id=" + listeAttenteId);
            }

            RDV rdv = rdvRepository.findById(rdvId);
            if (rdv == null) {
                throw new ServiceException("Aucun RDV trouvé avec id=" + rdvId);
            }

            rdv.setListeAttenteId(listeAttenteId);
            rdv.setModifiePar(user);
            rdvRepository.update(rdv);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de l'ajout du RDV à la liste d'attente", e);
        }
    }

    @Override
    public void retirerRdvDeListe(Long rdvId, String user) throws ServiceException {
        try {
            RDV rdv = rdvRepository.findById(rdvId);
            if (rdv == null) {
                throw new ServiceException("Aucun RDV trouvé avec id=" + rdvId);
            }

            rdv.setListeAttenteId(null);
            rdv.setModifiePar(user);
            rdvRepository.update(rdv);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du retrait du RDV de la liste d'attente", e);
        }
    }

    @Override
    public List<RDV> listerRdvsDeListe(Long listeAttenteId) throws ServiceException {
        try {
            return rdvRepository.findByListeAttenteId(listeAttenteId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du listing des RDV de la liste d'attente id=" + listeAttenteId, e);
        }
    }
}
