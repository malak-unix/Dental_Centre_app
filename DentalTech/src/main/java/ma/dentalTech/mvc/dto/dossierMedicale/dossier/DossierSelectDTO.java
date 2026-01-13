package ma.dentalTech.mvc.dto.dossierMedicale.dossier;

/**
 * DTO pour afficher les dossiers médicaux dans un dropdown/combobox.
 * Utilisé dans les formulaires (ex: ConsultationAddFormUI).
 */
public record DossierSelectDTO(
        Long dossierId,
        Long patientId,
        String patientNomComplet  // "Nom Prénom" pour affichage
) {
    @Override
    public String toString() {
        return patientNomComplet + " - Dossier #" + dossierId;
    }
}
