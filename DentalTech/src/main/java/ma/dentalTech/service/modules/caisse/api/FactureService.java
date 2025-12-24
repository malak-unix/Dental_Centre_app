package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.mvc.dto.caisse.*;

import java.time.LocalDateTime;
import java.util.List;

public interface FactureService {

    // ===== Ancien (ne pas casser l’existant)
    Facture createFacture(Facture facture) throws ServiceException;
    Facture updateFacture(Facture facture) throws ServiceException;
    void annulerFacture(Long id) throws ServiceException;
    Facture getFactureById(Long id) throws ServiceException;
    List<Facture> getFacturesBetween(LocalDateTime start, LocalDateTime end) throws ServiceException;
    void enregistrerPaiement(Long factureId, Double montantPaye) throws ServiceException;

    // ===== Nouveau (aligné remarques prof / maquettes)
    Facture createFactureFromDTO(FactureCreateDTO dto) throws ServiceException;
    Facture updateFactureFromDTO(Long factureId, FactureUpdateDTO dto) throws ServiceException;
    void payerFacture(Long factureId, FacturePaiementDTO dto) throws ServiceException;

    // Impression (au minimum : DTO + stub pdf)
    FacturePrintDTO getFactureForPrint(Long factureId) throws ServiceException;
    byte[] exportFacturePdf(Long factureId) throws ServiceException;



}


