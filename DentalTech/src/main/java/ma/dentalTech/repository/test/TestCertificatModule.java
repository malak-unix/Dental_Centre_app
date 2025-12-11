package ma.dentalTech.repository.test;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.repository.modules.certificat.api.CertificatRepository;

import java.time.LocalDate;
import java.util.List;

public class TestCertificatModule {

    private final CertificatRepository certificatRepo;
    private Long idCertificat;

    public TestCertificatModule() {
        this.certificatRepo = ApplicationContext.getBean(CertificatRepository.class);
    }

    // INSERT
    void insertProcess() {

        System.out.println("=== [Certificat] INSERT ===");

        Certificat c = Certificat.builder()
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now().plusDays(3))
                .duree(3)
                .noteMedecin("Certificat test (repos 3 jours)")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        certificatRepo.create(c);
        idCertificat = c.getId();
        System.out.println("Certificat créé id=" + idCertificat);
    }

    // SELECT
    void selectProcess() {

        System.out.println("=== [Certificat] SELECT ===");

        if (idCertificat != null) {
            Certificat c = certificatRepo.findById(idCertificat);
            System.out.println("Certificat trouvé : " + c);
        }

        List<Certificat> all = certificatRepo.findAll();
        System.out.println("Nb total certificats = " + all.size());
    }

    // UPDATE
    void updateProcess() {

        System.out.println("=== [Certificat] UPDATE ===");
        if (idCertificat == null) return;

        Certificat c = certificatRepo.findById(idCertificat);
        if (c == null) return;

        c.setDuree(c.getDuree() + 1);
        c.setNoteMedecin("Certificat mis à jour (repos prolongé)");
        c.setModifiePar("TEST_UPDATE");

        certificatRepo.update(c);
        System.out.println("Certificat après update : " + certificatRepo.findById(idCertificat));
    }

    // DELETE
    void deleteProcess() {

        System.out.println("=== [Certificat] DELETE ===");
        if (idCertificat == null) return;

        certificatRepo.deleteById(idCertificat);
        System.out.println("Certificat supprimé id=" + idCertificat);
    }

    public static void main(String[] args) {
        TestCertificatModule t = new TestCertificatModule();
        t.insertProcess();
        t.selectProcess();
        t.updateProcess();
        t.selectProcess();
        t.deleteProcess();
    }
}
