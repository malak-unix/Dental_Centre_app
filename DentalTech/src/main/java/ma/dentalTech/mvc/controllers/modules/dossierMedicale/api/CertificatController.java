package ma.dentalTech.mvc.controllers.modules.dossierMedicale.api;

import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatListRequestDTO;

import java.util.List;

/**
 * Controller MVC (Swing) : expose des méthodes simples pour l'UI.
 */
public interface CertificatController {

    /**
     * Retourne la liste à afficher dans l'écran "Certificats" (filtre par médecin, patient, dates...)
     */
    List<CertificatListItemDTO> searchForList(CertificatListRequestDTO in);

    /**
     * Création d'un certificat.
     */
    Long create(CertificatDTO certificat, String username);

    /**
     * Mise à jour d'un certificat.
     */
    void update(CertificatDTO certificat, String username);

    /**
     * Suppression d'un certificat.
     */
    void delete(Long certificatId);

    /**
     * Récupération d'un certificat par ID.
     */
    CertificatDTO getById(Long certificatId);

    /**
     * Récupération des détails complets d'un certificat.
     */
    CertificatDetailDTO getDetail(Long certificatId);
}
