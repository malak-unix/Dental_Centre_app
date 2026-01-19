package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.dentalTech.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.repository.modules.users.impl.MedecinRepositoryImpl;
import ma.dentalTech.service.modules.caisse.api.CaisseValidationService;
import ma.dentalTech.service.modules.caisse.api.FacturePdfService;
import ma.dentalTech.service.modules.caisse.api.FactureServiceV2;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FactureServiceV2Impl implements FactureServiceV2 {

    private final FactureRepository factureRepository;
    private final FacturePdfService facturePdfService;

    private final CaisseValidationService validation =
            ApplicationContext.getBean(CaisseValidationService.class);

    @Override
    public CaisseFactureRowDTO create(FactureCreateDTO dto) {
        validation.validateFactureCreate(dto);

        Facture f = Facture.builder()
                .consultationId(dto.getConsultationId())
                .dateFacture(dto.getDateFacture())
                .totalFacture(toDouble(dto.getTotalFacture())) // ✅ entity Double
                .totalPaye(0.0)
                .statut(StatutFacture.NON_PAYEE)
                .build();

        factureRepository.create(f);
        return toRow(f,
                new ConsultationRepositoryImpl(),
                new DossierMedicalRepositoryImpl(),
                new PatientRepositoryImpl(),
                new MedecinRepositoryImpl(),
                new HashMap<>(),
                new HashMap<>());
    }

    @Override
    public CaisseFactureRowDTO getById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        Facture f = factureRepository.findById(id);
        if (f == null) throw new IllegalArgumentException("Facture introuvable");
        return toRow(f,
                new ConsultationRepositoryImpl(),
                new DossierMedicalRepositoryImpl(),
                new PatientRepositoryImpl(),
                new MedecinRepositoryImpl(),
                new HashMap<>(),
                new HashMap<>());
    }

    @Override
    public List<CaisseFactureRowDTO> listBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end obligatoires");
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être après start");

        ConsultationRepository consultationRepo = new ConsultationRepositoryImpl();
        DossierMedicalRepository dossierRepo = new DossierMedicalRepositoryImpl();
        PatientRepository patientRepo = new PatientRepositoryImpl();
        MedecinRepository medecinRepo = new MedecinRepositoryImpl();

        Map<Long, String> patientNames = new HashMap<>();
        Map<Long, String> medecinNames = new HashMap<>();

        return factureRepository.findByDateBetween(start, end)
                .stream()
                .map(f -> toRow(f, consultationRepo, dossierRepo, patientRepo, medecinRepo, patientNames, medecinNames))
                .collect(Collectors.toList());
    }

    @Override
    public CaisseFactureRowDTO payer(Long factureId, FacturePaiementDTO dto) {
        if (factureId == null) throw new IllegalArgumentException("factureId obligatoire");
        validation.validatePaiement(dto);

        Facture f = factureRepository.findById(factureId);
        if (f == null) throw new IllegalArgumentException("Facture introuvable id=" + factureId);

        double total = nvl(f.getTotalFacture());
        double paye = nvl(f.getTotalPaye());
        double montant = dto.getMontant().doubleValue();

        if (montant <= 0) throw new IllegalArgumentException("Montant doit être > 0");

        double newPaye = paye + montant;
        if (newPaye > total + 0.0001) {
            throw new IllegalArgumentException("Paiement dépasse le total facture");
        }

        f.setTotalPaye(newPaye);

        if (Math.abs(newPaye - total) < 0.0001) {
            f.setStatut(StatutFacture.PAYEE);
        } else if (newPaye > 0) {
            f.setStatut(StatutFacture.PARTIEL);
        } else {
            f.setStatut(StatutFacture.NON_PAYEE);
        }

        factureRepository.update(f);
        return toRow(f,
                new ConsultationRepositoryImpl(),
                new DossierMedicalRepositoryImpl(),
                new PatientRepositoryImpl(),
                new MedecinRepositoryImpl(),
                new HashMap<>(),
                new HashMap<>());
    }

    @Override
    public FacturePrintDTO getForPrint(Long factureId) {
        if (factureId == null) throw new IllegalArgumentException("factureId obligatoire");
        Facture f = factureRepository.findById(factureId);
        if (f == null) throw new IllegalArgumentException("Facture introuvable");

        double total = nvl(f.getTotalFacture());
        double paye = nvl(f.getTotalPaye());
        double reste = Math.max(0.0, total - paye);

        return FacturePrintDTO.builder()
                .numeroFacture(String.valueOf(f.getId()))
                .dateFacture(f.getDateFacture())
                .consultationId(f.getConsultationId())
                .totalFacture(BigDecimal.valueOf(total))
                .totalPaye(BigDecimal.valueOf(paye))
                .reste(BigDecimal.valueOf(reste))
                .statut(f.getStatut() == null ? null : f.getStatut().name())
                .build();
    }

    @Override
    public byte[] exportPdf(Long factureId) {
        FacturePrintDTO dto = getForPrint(factureId);
        return facturePdfService.generateFacturePdf(dto);
    }

    // ========================= Helpers =========================

    private CaisseFactureRowDTO toRow(Facture f,
                                      ConsultationRepository consultationRepo,
                                      DossierMedicalRepository dossierRepo,
                                      PatientRepository patientRepo,
                                      MedecinRepository medecinRepo,
                                      Map<Long, String> patientNames,
                                      Map<Long, String> medecinNames) {
        double total = nvl(f.getTotalFacture());
        double paye = nvl(f.getTotalPaye());
        double reste = Math.max(0.0, total - paye);

        CaisseFactureRowDTO row = CaisseFactureRowDTO.builder()
                .factureId(f.getId())
                .consultationId(f.getConsultationId())
                .dateFacture(f.getDateFacture())
                .totalFacture(BigDecimal.valueOf(total))
                .totalPaye(BigDecimal.valueOf(paye))
                .reste(BigDecimal.valueOf(reste))
                .montant(total)
                .numeroFacture(f.getId() == null ? null : ("F-" + f.getId()))
                .dateEmission(f.getDateFacture())
                .statut(f.getStatut() == null ? null : f.getStatut().name())
                .canView(true)
                .canPrint(true)
                .canPay(f.getStatut() != StatutFacture.PAYEE)
                .canCancel(false)
                .build();

        enrichRow(row, f, consultationRepo, dossierRepo, patientRepo, medecinRepo, patientNames, medecinNames);
        return row;
    }

    private void enrichRow(CaisseFactureRowDTO row,
                           Facture f,
                           ConsultationRepository consultationRepo,
                           DossierMedicalRepository dossierRepo,
                           PatientRepository patientRepo,
                           MedecinRepository medecinRepo,
                           Map<Long, String> patientNames,
                           Map<Long, String> medecinNames) {
        if (row == null || f == null) return;
        Long consultationId = f.getConsultationId();
        if (consultationId == null) return;

        ma.dentalTech.entities.dossierMedical.Consultation c = consultationRepo.findById(consultationId);
        if (c == null) return;
        Long dossierId = c.getDossierId();
        if (dossierId == null) return;

        ma.dentalTech.entities.dossierMedical.DossierMedical d = dossierRepo.findById(dossierId);
        if (d == null) return;

        Long patientId = d.getPatientId();
        Long medecinId = d.getMedecinId();

        String patientNom = resolvePatientName(patientId, patientRepo, patientNames);
        String medecinNom = resolveMedecinName(medecinId, medecinRepo, medecinNames);

        row.setPatientNom(patientNom);
        row.setMedecinNom(medecinNom);
    }

    private String resolvePatientName(Long patientId, PatientRepository repo, Map<Long, String> cache) {
        if (patientId == null) return null;
        String cached = cache.get(patientId);
        if (cached != null) return cached;
        ma.dentalTech.entities.patient.Patient p = repo.findById(patientId);
        if (p == null) return null;
        String name = (safe(p.getNom()) + " " + safe(p.getPrenom())).trim();
        if (name.isBlank()) name = "Patient #" + patientId;
        cache.put(patientId, name);
        return name;
    }

    private String resolveMedecinName(Long medecinId, MedecinRepository repo, Map<Long, String> cache) {
        if (medecinId == null) return null;
        String cached = cache.get(medecinId);
        if (cached != null) return cached;
        ma.dentalTech.entities.users.Medecin m = repo.findById(medecinId);
        if (m == null) return null;
        String name = (safe(m.getNom()) + " " + safe(m.getPrenom())).trim();
        if (name.isBlank()) name = "Medecin #" + medecinId;
        cache.put(medecinId, name);
        return name;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private double nvl(Double v) { return v == null ? 0.0 : v; }
    private Double toDouble(BigDecimal bd) { return bd == null ? null : bd.doubleValue(); }
}
