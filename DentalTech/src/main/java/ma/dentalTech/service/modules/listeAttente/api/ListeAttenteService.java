package ma.dentalTech.service.modules.listeAttente.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.listeDattente.ListeAttente;
import ma.dentalTech.entities.rdv.RDV;

import java.util.List;

public interface ListeAttenteService {

    ListeAttente creerListe(String nomListe, String user) throws ServiceException;

    ListeAttente trouverParId(Long id) throws ServiceException;

    ListeAttente trouverParNom(String nomListe) throws ServiceException;

    List<ListeAttente> listerToutes() throws ServiceException;

    void ajouterRdvAListe(Long listeAttenteId, Long rdvId, String user) throws ServiceException;

    void retirerRdvDeListe(Long rdvId, String user) throws ServiceException;

    List<RDV> listerRdvsDeListe(Long listeAttenteId) throws ServiceException;
}
