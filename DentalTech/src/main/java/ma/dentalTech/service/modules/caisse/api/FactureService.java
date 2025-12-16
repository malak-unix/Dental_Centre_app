package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.dossierMedical.Facture;

import java.time.LocalDateTime;
import java.util.List;

public interface FactureService {

    Facture createFacture(Facture facture) throws ServiceException;

    Facture updateFacture(Facture facture) throws ServiceException;

    void annulerFacture(Long id) throws ServiceException;

    Facture getFactureById(Long id) throws ServiceException;

    List<Facture> getFacturesBetween(LocalDateTime start, LocalDateTime end) throws ServiceException;

    void enregistrerPaiement(Long factureId, Double montantPaye) throws ServiceException;
}
