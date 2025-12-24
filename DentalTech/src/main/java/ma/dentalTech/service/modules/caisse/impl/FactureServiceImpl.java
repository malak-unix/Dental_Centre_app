package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.service.modules.caisse.api.FactureService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;

    // =========================
    // CREATE
    // =========================
    @Override
    public CaisseFactureDTO createFacture(FactureCreateDTO dto) {

        // 🔒 VALIDATIONS (obligatoires)
        if (dto.getPatientId() == null)
            throw new DaoException("Patient obligatoire");

        if (dto.getLignes() == null || dto.getLignes().isEmpty())
            throw new DaoException("Facture sans lignes");

        Facture facture = factureRepository.createFromDTO(dto);

        return mapToCaisseFactureDTO(facture);
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public CaisseFactureDTO updateFacture(Long factureId, FactureUpdateDTO dto) {

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new DaoException("Facture introuvable"));

        if (facture.isPayee())
            throw new DaoException("Facture payée non modifiable");

        factureRepository.updateFromDTO(facture, dto);
        return mapToCaisseFactureDTO(facture);
    }

    // =========================
    // ANNULATION
    // =========================
    @Override
    public void cancelFacture(Long factureId) {

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new DaoException("Facture introuvable"));

        if (facture.isPayee())
            throw new DaoException("Impossible d’annuler une facture payée");

        factureRepository.cancel(factureId);
    }

    // =========================
    // PAIEMENT
    // =========================
    @Override
    public void payerFacture(Long factureId, FacturePaiementDTO dto) {

        if (dto.getMontant() == null || dto.getMontant() <= 0)
            throw new DaoException("Montant invalide");

        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new DaoException("Facture introuvable"));

        factureRepository.enregistrerPaiement(factureId, dto);
    }

    // =========================
    // LISTE
    // =========================
    @Override
    public List<CaisseFactureDTO> searchFactures(CaisseDashboardRequestDTO dto) {

        return factureRepository.search(
                        dto.getDateDebut(),
                        dto.getDateFin(),
                        dto.getMedecinId(),
                        dto.getStatut(),
                        dto.getSearch()
                ).stream()
                .map(this::mapToCaisseFactureDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // IMPRESSION
    // =========================
    @Override
    public FacturePrintDTO getFactureForPrint(Long factureId) {

        Facture facture = factureRepository.findByIdWithDetails(factureId)
                .orElseThrow(() -> new DaoException("Facture introuvable"));

        return factureRepository.mapToPrintDTO(facture);
    }

    @Override
    public byte[] exportFacturePdf(Long factureId) {

        FacturePrintDTO dto = getFactureForPrint(factureId);

        // ici tu appelleras plus tard PdfService (iText/OpenPDF)
        return PdfFactureGenerator.generate(dto);
    }

    // =========================
    // MAPPER INTERNE
    // =========================
    private CaisseFactureDTO mapToCaisseFactureDTO(Facture f) {

        return CaisseFactureDTO.builder()
                .factureId(f.getId())
                .nom(f.getPatient().getNom())
                .prenom(f.getPatient().getPrenom())
                .montant(f.getTotalTtc())
                .dateEmission(f.getDateEmission())
                .statut(f.getStatut().name())
                .reste(f.getResteAPayer())
                .canView(true)
                .canPrint(true)
                .canPay(!f.isPayee())
                .canCancel(!f.isPayee())
                .build();
    }
}
