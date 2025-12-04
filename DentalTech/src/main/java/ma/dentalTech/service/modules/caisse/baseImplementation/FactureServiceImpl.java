package ma.dentalTech.service.modules.caisse.baseImplementation;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.mvc.dto.FactureDTO;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.service.modules.caisse.api.FactureService;

@Data @AllArgsConstructor @NoArgsConstructor
public class FactureServiceImpl implements FactureService {

    private FactureRepository factureRepository;

    @Override
    public void createFacture(Facture facture) {
        // Logique métier: validation des montants, calculs initiaux, etc.
        // Exemple de validation: Validators.notBlank(facture.getConsultationId(), "ID consultation");
        factureRepository.create(facture);
    }

    @Override
    public List<FactureDTO> getAllFacturesAsDTO() {
        // Récupère toutes les entités et les mappe vers des DTOs
        return factureRepository.findAll().stream()
                .map(this::mapFactureToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void payerFacture(Long factureId, Double montantPaye) {
        Facture f = factureRepository.findById(factureId);
        if (f == null) throw new RuntimeException("Facture non trouvée");

        double nouveauPaye = f.getTotalePaye() + montantPaye;
        f.setTotalePaye(nouveauPaye);

        if (nouveauPaye >= f.getTotaleFacture()) {
            f.setStatus(ma.dentalTech.entities.enums.StatutFacture.PAYEE);
            f.setReste(0.0);
        } else if (nouveauPaye > 0) {
            f.setStatus(ma.dentalTech.entities.enums.StatutFacture.PARTIELLEMENT_PAYEE);
            f.setReste(f.getTotaleFacture() - nouveauPaye);
        }

        factureRepository.update(f);
    }

    @Override
    public String generateFactureReport(Long factureId) {
        // Logique de génération de PDF (iTextPDF mentionné dans le Readme)
        return "Facture_" + factureId + ".pdf";
    }

    // Méthode de mapping pour convertir l'Entité en DTO
    private FactureDTO mapFactureToDTO(Facture f) {
        // TODO: Implémenter la récupération du nom du patient via un PatientRepository
        String nomPatient = "Inconnu";

        return FactureDTO.builder()
                .id(f.getId())
                .nomCompletPatient(nomPatient)
                .total(f.getTotaleFacture())
                .resteAPayer(f.getReste())
                .statut(f.getStatus())
                .dateFactureFormatee(f.getDateFacture().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .build();
    }
}