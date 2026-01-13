package ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.SituationFinanciereController;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.FactureDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.SituationFinanciereDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.SituationFinanciereListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.SituationFinanciereListRequestDTO;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.repository.modules.caisse.impl.FactureRepositoryImpl;
import ma.dentalTech.repository.modules.caisse.impl.SituationFinanciereRepositoryImpl;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.dentalTech.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.dentalTech.repository.modules.dossierMedical.impl.InterventionMedecinRepositoryImpl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SituationFinanciereControllerImpl implements SituationFinanciereController {

    private final SituationFinanciereRepository situationRepo;
    private final DossierMedicalRepository dossierRepo;
    private final FactureRepository factureRepo;
    private final ConsultationRepository consultationRepo;
    private final InterventionMedecinRepository interventionRepo;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public SituationFinanciereControllerImpl() {
        this.situationRepo = new SituationFinanciereRepositoryImpl();
        this.dossierRepo = new DossierMedicalRepositoryImpl();
        this.factureRepo = new FactureRepositoryImpl();
        this.consultationRepo = new ConsultationRepositoryImpl();
        this.interventionRepo = new InterventionMedecinRepositoryImpl();
    }

    @Override
    public List<SituationFinanciereListItemDTO> searchForList(SituationFinanciereListRequestDTO request) {
        try {
            // Récupérer les dossiers du médecin
            List<DossierMedical> dossiers = dossierRepo.findByMedecinId(request.medecinId());
            
            List<SituationFinanciereListItemDTO> result = new ArrayList<>();
            
            for (DossierMedical dossier : dossiers) {
                // Filtrer par mot-clé patient si fourni
                if (request.patientKeyword() != null && !request.patientKeyword().trim().isEmpty()) {
                    String keyword = request.patientKeyword().toLowerCase();
                    String patientNom = (dossier.getPatient().getNom() + " " + dossier.getPatient().getPrenom()).toLowerCase();
                    if (!patientNom.contains(keyword)) {
                        continue;
                    }
                }

                SituationFinanciere sf = situationRepo.findByDossierId(dossier.getId());
                if (sf == null) continue; // Pas de situation financière pour ce dossier

                SituationFinanciereListItemDTO item = new SituationFinanciereListItemDTO();
                item.setSituationFinanciereId(sf.getId());
                item.setDossierId(dossier.getId());
                item.setPatientId(dossier.getPatient().getId());
                item.setPatientNomComplet(dossier.getPatient().getNom() + " " + dossier.getPatient().getPrenom());

                // Recalculer les totaux à partir des factures et des actes liés aux consultations
                List<Facture> factures = factureRepo.findByDossierId(dossier.getId());

                double totalFacturesCalcule = 0.0;
                double totalPayeCalcule = 0.0;

                // Récupérer la dernière facture
                if (!factures.isEmpty()) {
                    Facture derniereFacture = factures.stream()
                            .sorted((f1, f2) -> {
                                if (f1.getDateFacture() == null) return 1;
                                if (f2.getDateFacture() == null) return -1;
                                return f2.getDateFacture().compareTo(f1.getDateFacture());
                            })
                            .findFirst()
                            .orElse(null);

                    if (derniereFacture != null) {
                        String numeroFacture = "F" + String.format("%04d", derniereFacture.getId());
                        String dateFacture = derniereFacture.getDateFacture() != null ?
                                derniereFacture.getDateFacture().format(DATE_FMT) : "";
                        item.setDerniereFacture(numeroFacture + " - " + dateFacture);
                    }
                }

                // Calcul des totaux/reste basés sur les actes réalisés pour chaque consultation facturée
                for (Facture facture : factures) {
                    double totalFacture = calculeMontantFacture(facture);
                    double paye = facture.getTotalPaye() != null ? facture.getTotalPaye() : 0.0;
                    totalFacturesCalcule += totalFacture;
                    totalPayeCalcule += paye;
                }

                double credit = sf.getCredit() != null ? sf.getCredit() : 0.0;
                item.setSolde(totalFacturesCalcule - totalPayeCalcule + credit); // solde affiché dans la liste

                // TODO: Calculer le prochain paiement (pour l'instant null)
                item.setProchainPaiement(null);

                result.add(item);
            }

            return result;

        } catch (Exception e) {
            throw new ControllerException("Erreur lors de la recherche des situations financières", e);
        }
    }

    @Override
    public SituationFinanciereDetailDTO getDetail(Long situationFinanciereId) {
        try {
            SituationFinanciere sf = situationRepo.findById(situationFinanciereId);
            if (sf == null) {
                throw new ControllerException("Situation financière introuvable avec l'ID: " + situationFinanciereId);
            }

            DossierMedical dossier = dossierRepo.findById(sf.getDossierId());
            if (dossier == null) {
                throw new ControllerException("Dossier médical introuvable");
            }

            String patientNomComplet = dossier.getPatient().getNom() + " " + dossier.getPatient().getPrenom();

            // Récupérer les factures avec recalcul des montants à partir des actes
            List<Facture> factures = factureRepo.findByDossierId(sf.getDossierId());
            List<FactureDetailDTO> factureDTOs = new ArrayList<>();

            double totalFacturesCalcule = 0.0;
            double totalPayeCalcule = 0.0;

            for (Facture facture : factures) {
                Consultation consultation = null;
                String consultationLibelle = "N/A";
                if (facture.getConsultationId() != null) {
                    consultation = consultationRepo.findById(facture.getConsultationId());
                    if (consultation != null) {
                        consultationLibelle = "Consultation #" + consultation.getId() + " - " +
                                (consultation.getStatus() != null ? consultation.getStatus() : "");
                    }
                }

                String numeroFacture = "F" + String.format("%04d", facture.getId());

                double totalFacture = calculeMontantFacture(facture);
                double totalPaye = facture.getTotalPaye() != null ? facture.getTotalPaye() : 0.0;
                double reste = totalFacture - totalPaye;

                totalFacturesCalcule += totalFacture;
                totalPayeCalcule += totalPaye;

                factureDTOs.add(new FactureDetailDTO(
                        facture.getId(),
                        numeroFacture,
                        facture.getDateFacture(),
                        totalFacture,
                        totalPaye,
                        reste,
                        facture.getStatut(),
                        facture.getConsultationId(),
                        consultationLibelle
                ));
            }

            double credit = sf.getCredit() != null ? sf.getCredit() : 0.0;
            double soldeCalcule = totalFacturesCalcule - totalPayeCalcule + credit;

            return new SituationFinanciereDetailDTO(
                    sf.getId(),
                    sf.getDossierId(),
                    dossier.getPatient().getId(),
                    patientNomComplet,
                    sf.getMedecinId(),
                    totalFacturesCalcule,
                    totalPayeCalcule,
                    credit,
                    soldeCalcule,
                    sf.getStatut(),
                    factureDTOs
            );

        } catch (Exception e) {
            throw new ControllerException("Erreur lors de la récupération des détails: " + e.getMessage(), e);
        }
    }

    @Override
    public void reset(Long situationFinanciereId, String username) {
        try {
            SituationFinanciere sf = situationRepo.findById(situationFinanciereId);
            if (sf == null) {
                throw new ControllerException("Situation financière introuvable");
            }

            situationRepo.resetByDossierId(sf.getDossierId(), username);
        } catch (Exception e) {
            throw new ControllerException("Erreur lors de la réinitialisation: " + e.getMessage(), e);
        }
    }

    /**
     * Calcule le montant d'une facture à partir des actes réalisés sur la consultation liée.
     * - Si des interventions existent : somme des prix_patient.
     * - Sinon : utilise le total_facture déjà enregistré (prix consultation sans acte).
     */
    private double calculeMontantFacture(Facture facture) {
        if (facture == null) return 0.0;

        double totalInterventions = 0.0;
        if (facture.getConsultationId() != null) {
            var interventions = interventionRepo.findByConsultationId(facture.getConsultationId());
            for (var inter : interventions) {
                totalInterventions += inter.getPrixDePatient() != null ? inter.getPrixDePatient() : 0.0;
            }
        }

        double totalFactureEnBase = facture.getTotalFacture() != null ? facture.getTotalFacture() : 0.0;

        // Si des actes existent, on prend la somme ; sinon on garde le prix de consultation.
        return totalInterventions > 0 ? totalInterventions : totalFactureEnBase;
    }
}
