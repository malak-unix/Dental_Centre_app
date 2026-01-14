package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.ApplicationContext;
// ⚠️ adapte si ta classe connexion n'a pas ce nom
import ma.dentalTech.configuration.SessionFactory;

import ma.dentalTech.entities.agenda.*;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.entities.patient.*;
import ma.dentalTech.entities.enums.*;

import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.repository.modules.dossierMedical.impl.*;

import ma.dentalTech.repository.modules.agenda.api.*;
import ma.dentalTech.repository.modules.agenda.impl.*;

import ma.dentalTech.repository.modules.patient.api.*;
import ma.dentalTech.repository.modules.patient.impl.*;

import ma.dentalTech.entities.cabinet.*;
import ma.dentalTech.entities.enums.StatutFacture;

import ma.dentalTech.repository.modules.caisse.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestRepo {

    // ==========================
    // Repos (dossierMedical)
    // ==========================
    private final DossierMedicalRepository dossierRepo = new DossierMedicalRepositoryImpl();
    private final DocumentMedicalRepository documentRepo = new DocumentMedicalRepositoryImpl();
    private final CertificatRepository certificatRepo = new CertificatRepositoryImpl();
    private final ActeRepository acteRepo = new ActeRepositoryImpl();
    private final ConsultationRepository consultationRepo = new ConsultationRepositoryImpl();
    private final InterventionMedecinRepository interventionRepo = new InterventionMedecinRepositoryImpl();
    private final OrdonnanceRepository ordonnanceRepo = new OrdonnanceRepositoryImpl();
    private final MedicamentRepository medicamentRepo = new MedicamentRepositoryImpl();
    private final PrescriptionRepository prescriptionRepo = new PrescriptionRepositoryImpl();

    // ==========================
    // Repos (caisse)
    // ==========================
    private final FactureRepository factureRepo = ApplicationContext.getBean(FactureRepository.class);
    private final ChargesRepository chargesRepo = ApplicationContext.getBean(ChargesRepository.class);
    private final RevenuesRepository revenusRepo = ApplicationContext.getBean(RevenuesRepository.class);
    private final SituationFinanciereRepository sitRepo = ApplicationContext.getBean(SituationFinanciereRepository.class);

    // ==========================
    // Repos (agenda)
    // ==========================
    private final AgendaMensuelRepository agendaRepo = new AgendaMensuelRepositoryImpl();
    private final DetailJourneeRepository detailJourneeRepo = new DetailJourneeRepositoryImpl();
    private final PlageHoraireRepository plageHoraireRepo = new PlageHoraireRepositoryImpl();
    private final RdvRepository rdvRepo = new RdvRepositoryImpl();
    private final ListeAttenteRepository listeAttenteRepo = new ListeAttenteRepositoryImpl();

    // ==========================
    // Repos (patient / antecedent)
    // ==========================
    private final PatientRepository patientRepo = new PatientRepositoryImpl();
    private final AntecedentRepository antecedentRepo = new AntecedentRepositoryImpl();

    // =========================================================
    // CONNEXION JDBC (via votre infra)
    // =========================================================
    private Connection getConn() throws SQLException {
        return SessionFactory.getInstance().getConnection();
    }


    // =========================================================
    // UTIL : récupère un id généré
    // =========================================================
    private long extractGeneratedId(PreparedStatement ps) throws SQLException {
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) return keys.getLong(1);
        }
        throw new SQLException("Aucun ID généré retourné.");
    }

    private String uniq(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }

    // =========================================================
    // ENSURE : CABINET MEDICAL
    // =========================================================
    private Long ensureCabinetId() {
        try (Connection conn = getConn()) {

            // 1) si existe déjà, prendre le premier
            try (PreparedStatement st = conn.prepareStatement("SELECT id FROM cabinet_medical LIMIT 1");
                 ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }

            // 2) sinon créer
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO cabinet_medical (nom, email, adresse, telephone1, cree_par, modifie_par) VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, "CABINET_TEST");
                ps.setString(2, "cabinet@test.ma");
                ps.setString(3, "Rabat");
                ps.setString(4, "0600000000");
                ps.setString(5, "TEST");
                ps.setString(6, "TEST");
                ps.executeUpdate();
                Long id = extractGeneratedId(ps);
                System.out.println("✅ Cabinet créé id=" + id);
                return id;
            }

        } catch (Exception e) {
            throw new RuntimeException("ensureCabinetId() failed", e);
        }
    }

    // =========================================================
    // ENSURE : ROLE (MEDECIN)
    // =========================================================
    private Long ensureRoleMedecinId() {
        try (Connection conn = getConn()) {

            try (PreparedStatement st = conn.prepareStatement("SELECT id FROM role WHERE libelle='MEDECIN' LIMIT 1");
                 ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO role (libelle, privileges, cree_par, modifie_par) VALUES ('MEDECIN', ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, "ALL");
                ps.setString(2, "TEST");
                ps.setString(3, "TEST");
                ps.executeUpdate();
                Long id = extractGeneratedId(ps);
                System.out.println("✅ Role MEDECIN créé id=" + id);
                return id;
            }

        } catch (Exception e) {
            throw new RuntimeException("ensureRoleMedecinId() failed", e);
        }
    }

    // =========================================================
    // ENSURE : MEDECIN (utilisateur + staff + medecin)
    // =========================================================
    private Long ensureMedecinId() {
        try (Connection conn = getConn()) {

            // 1) si un medecin existe déjà
            try (PreparedStatement st = conn.prepareStatement("SELECT id FROM medecin LIMIT 1");
                 ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }

            Long cabinetId = ensureCabinetId();
            Long roleId = ensureRoleMedecinId();

            // 2) créer utilisateur
            String login = uniq("medecin");
            String email = login + "@test.ma";

            long utilisateurId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO utilisateur (nom, prenom, email, adresse, tel, sexe, login, mot_de_passe, date_naissance, actif, role_id, cree_par, modifie_par) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, "MED");
                ps.setString(2, "TEST");
                ps.setString(3, email);
                ps.setString(4, "Rabat");
                ps.setString(5, "0600001234");
                ps.setString(6, "AUTRE");
                ps.setString(7, login);

                // mot_de_passe : pour test uniquement (si vous stockez bcrypt ailleurs, ce n'est pas grave pour FK)
                ps.setString(8, "123456");
                ps.setDate(9, Date.valueOf(LocalDate.of(1990, 1, 1)));
                ps.setBoolean(10, true);
                ps.setLong(11, roleId);
                ps.setString(12, "TEST");
                ps.setString(13, "TEST");

                ps.executeUpdate();
                utilisateurId = extractGeneratedId(ps);
            }

            // 3) créer staff (hérite utilisateur)
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO staff (id, salaire, prime, date_recrutement, solde_conge, cabinet_id, cree_par, modifie_par) " +
                            "VALUES (?,?,?,?,?,?,?,?)"
            )) {
                ps.setLong(1, utilisateurId);
                ps.setBigDecimal(2, new java.math.BigDecimal("0"));
                ps.setBigDecimal(3, new java.math.BigDecimal("0"));
                ps.setDate(4, Date.valueOf(LocalDate.now()));
                ps.setInt(5, 0);
                ps.setLong(6, cabinetId);
                ps.setString(7, "TEST");
                ps.setString(8, "TEST");
                ps.executeUpdate();
            }

            // 4) créer medecin (hérite staff)
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO medecin (id, specialite, cree_par, modifie_par) VALUES (?,?,?,?)"
            )) {
                ps.setLong(1, utilisateurId);
                ps.setString(2, "Généraliste");
                ps.setString(3, "TEST");
                ps.setString(4, "TEST");
                ps.executeUpdate();
            }

            System.out.println("✅ Médecin créé id=" + utilisateurId + " (login=" + login + ")");
            return utilisateurId;

        } catch (Exception e) {
            throw new RuntimeException("ensureMedecinId() failed", e);
        }
    }

    // ==========================
    // TEST ANTECEDENT CRUD
    // ==========================
    void testAntecedentCrud() {
        System.out.println("\n=== TEST ANTECEDENT CRUD ===");

        Patient p = Patient.builder()
                .nom("P_TEST")
                .prenom("ANTE")
                .telephone("0600001111")
                .adresse("Rabat")
                .assurance(Assurance.AUCUNE)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        patientRepo.create(p);
        System.out.println("✅ Patient créé id=" + p.getId());

        Antecedents a = Antecedents.builder()
                .patientId(p.getId())
                .nom("Allergie test")
                .categorie("Allergie")
                .niveauDeRisque(NiveauDeRisque.ELEVE)
                .description("Test description")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        antecedentRepo.create(a);
        System.out.println("✅ Antecedent créé id=" + a.getId());

        Antecedents read = antecedentRepo.findById(a.getId());
        System.out.println("✅ Read antecedent: " + read);

        read.setDescription("Desc modifiée");
        read.setModifiePar("TEST");
        antecedentRepo.update(read);
        System.out.println("✅ Update OK");

        antecedentRepo.deleteById(read.getId());
        System.out.println("✅ Delete OK");
    }

    // =========================================================
    // INSERT PATIENT + ANTECEDENT
    // =========================================================
    void insertPatientAntecedent() {
        System.out.println("\n=== INSERT PATIENT / ANTECEDENT ===");

        Patient p = Patient.builder()
                .nom("PATIENT_TEST")
                .prenom("AGENDA")
                .telephone("0600009999")
                .adresse("Rabat")
                .assurance(Assurance.AUCUNE)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        patientRepo.create(p);
        System.out.println("✅ Patient créé ID=" + p.getId());

        Antecedents a = Antecedents.builder()
                .patientId(p.getId())
                .nom("Diabète")
                .categorie("Chronique")
                .niveauDeRisque(NiveauDeRisque.MOYEN)
                .description("Diabète type 2")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        antecedentRepo.create(a);
        System.out.println("✅ Antecedent créé ID=" + a.getId());
    }

    // =========================================================
    // INSERT AGENDA
    // =========================================================
    void insertAgenda() {
        System.out.println("\n=== INSERT AGENDA ===");

        Long medecinId = ensureMedecinId();

        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecinId(medecinId)
                .mois(Mois.JANVIER)
                .annee(2026)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        agendaRepo.create(agenda);

        DetailJournee jour = DetailJournee.builder()
                .agendaId(agenda.getId())
                .dateJour(LocalDate.now())
                .heureDebutTravail(LocalTime.of(9, 0))
                .heureFinTravail(LocalTime.of(17, 0))
                .etatJour(StatutJournee.OUVERT)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        detailJourneeRepo.create(jour);

        PlageHoraire ph = PlageHoraire.builder()
                .detailJourneeId(jour.getId())
                .heureDebut(LocalTime.of(10, 0))
                .heureFin(LocalTime.of(10, 30))
                .disponible(true)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        plageHoraireRepo.create(ph);

        ListeAttente la = ListeAttente.builder()
                .nom("Urgences")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        listeAttenteRepo.create(la);

        Patient patient = patientRepo.findAll().get(0);

        RDV rdv = RDV.builder()
                .patientId(patient.getId())
                .detailJourneeId(jour.getId())
                .listeAttenteId(la.getId())
                .dateRdv(LocalDate.now())
                .heure(LocalTime.of(10, 0))
                .motif("Contrôle")
                .statut(EtatRendezVous.PLANIFIE)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        rdvRepo.create(rdv);

        System.out.println("✅ Agenda complet créé");
    }

    void selectAgenda() {
        System.out.println("\n=== SELECT AGENDA ===");
        agendaRepo.findAll().forEach(System.out::println);
        rdvRepo.findAll().forEach(System.out::println);
    }

    void updateAgenda() {
        System.out.println("\n=== UPDATE AGENDA ===");

        AgendaMensuel a = agendaRepo.findAll().get(0);
        a.setAnnee(2027);
        a.setModifiePar("TEST");
        agendaRepo.update(a);

        RDV r = rdvRepo.findAll().get(0);
        r.setMotif("Contrôle MODIFIÉ");
        r.setModifiePar("TEST");
        rdvRepo.update(r);

        System.out.println("✅ Agenda mis à jour");
    }

    void deleteAgenda() {
        rdvRepo.findAll().forEach(r -> rdvRepo.deleteById(r.getId()));
        plageHoraireRepo.findAll().forEach(p -> plageHoraireRepo.deleteById(p.getId()));
        detailJourneeRepo.findAll().forEach(d -> detailJourneeRepo.deleteById(d.getId()));
        agendaRepo.findAll().forEach(a -> agendaRepo.deleteById(a.getId()));
        listeAttenteRepo.findAll().forEach(l -> listeAttenteRepo.deleteById(l.getId()));
        System.out.println("🧹 Agenda supprimé");
    }

    // =========================================================
    // TEST CAISSE - CHARGES CRUD
    // =========================================================
    void testChargesCrud() {
        System.out.println("\n=== TEST CHARGES CRUD ===");

        Long cabinetId = ensureCabinetId();

        Charges c = Charges.builder()
                .cabinetId(cabinetId)
                .titre("Charge TEST")
                .description("Desc charge test")
                .montant(123.45)
                .dateCharge(LocalDateTime.now())
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        chargesRepo.create(c);
        System.out.println("✅ Charge créée id=" + c.getId());

        Charges read = chargesRepo.findById(c.getId());
        System.out.println("✅ Read charge=" + read);

        read.setMontant(200.0);
        read.setModifiePar("TEST");
        chargesRepo.update(read);
        System.out.println("✅ Update OK");

        chargesRepo.deleteById(read.getId());
        System.out.println("✅ Delete OK");
    }

    // =========================================================
    // TEST CAISSE - REVENUS CRUD
    // =========================================================
    void testRevenusCrud() {
        System.out.println("\n=== TEST REVENUS CRUD ===");

        Long cabinetId = ensureCabinetId();

        Revenues r = Revenues.builder()
                .cabinetId(cabinetId)
                .titre("Revenu TEST")
                .description("Desc revenu test")
                .montant(500.0)
                .dateRevenu(LocalDateTime.now())
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        revenusRepo.create(r);
        System.out.println("✅ Revenu créé id=" + r.getId());

        Revenues read = revenusRepo.findById(r.getId());
        System.out.println("✅ Read revenu=" + read);

        read.setMontant(700.0);
        read.setModifiePar("TEST");
        revenusRepo.update(read);
        System.out.println("✅ Update OK");

        revenusRepo.deleteById(read.getId());
        System.out.println("✅ Delete OK");
    }

    // =========================================================
    // TEST CAISSE - FACTURE (read + paiement) + SITUATION FIN
    // =========================================================
    void testFactureEtSituation() {
        System.out.println("\n=== TEST FACTURE + SITUATION FINANCIERE ===");

        // On évite de CREATE facture (FK consultation), on teste sur la seed (id=1)
        Facture f = factureRepo.findById(1L);
        System.out.println("✅ Facture seed id=1 : " + f);

        boolean ok = factureRepo.updatePayment(
                1L,
                999.0,
                StatutFacture.PAYEE,
                "TEST"
        );
        System.out.println("✅ updatePayment OK ? " + ok);

        SituationFinanciere s = sitRepo.findById(1L);
        System.out.println("✅ Situation seed id=1 : " + s);

        SituationFinanciere byDossier = sitRepo.findByDossierId(1L);
        System.out.println("✅ Situation byDossierId(1) : " + byDossier);

        SituationFinanciere last = sitRepo.findLast();
        System.out.println("✅ Situation findLast : " + last);
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {

        TestRepo t = new TestRepo();

        t.testChargesCrud();
        t.testRevenusCrud();
        t.testFactureEtSituation();
        t.insertPatientAntecedent();
        t.testAntecedentCrud();

        t.insertAgenda();

        t.selectAgenda();
        t.updateAgenda();
        t.selectAgenda();

        t.deleteAgenda();
    }
}
