package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.mvc.controllers.modules.referentiel.api.ReferentielController;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.medicament.MedicamentDTO;
import ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO;
import ma.dentalTech.mvc.ui.modules.referentiel.ReferentielManagementPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestReferentielUi extends JFrame {

    public TestReferentielUi() {
        setTitle("Test Gestion Référentiels (Admin) - VRAIE UI AVEC MOCK");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Injecter le panneau réel avec un contrôleur Mock
        ReferentielManagementPanel panel = new ReferentielManagementPanel(new MockReferentielController());
        setContentPane(panel);
    }

    public static void main(String[] args) {
        // Appliquer le LookAndFeel pour voir le style réel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new TestReferentielUi().setVisible(true));
    }

    // --- MOCK CONTROLLER COMPLET ---
    static class MockReferentielController implements ReferentielController {

        private final List<ActeDTO> actes = new ArrayList<>();
        private final List<MedicamentDTO> medicaments = new ArrayList<>();
        private final List<RefAntecedentDTO> antecedents = new ArrayList<>();

        public MockReferentielController() {
            // Données initiales pour le test
            actes.add(new ActeDTO(1L, "Détartrage", "Soins", 300.0, "Nettoyage"));
            actes.add(new ActeDTO(2L, "Extraction", "Chirurgie", 200.0, "Dent simple"));

            medicaments.add(new MedicamentDTO(1L, "Doliprane", "Sanofi", "Antalgique", FormeMedicament.COMPRIME, true,
                    20.0, "Douleur"));
            antecedents.add(new RefAntecedentDTO(1L, "Diabète Type 2", "Endoc",
                    ma.dentalTech.entities.enums.NiveauDeRisque.MOYEN));
        }

        @Override
        public ActeDTO createActe(ActeDTO dto) {
            long newId = actes.size() + 1L;
            ActeDTO created = new ActeDTO(newId, dto.libelle(), dto.categorie(), dto.prixBase(), dto.description());
            actes.add(created);
            System.out.println("MOCK: Acte créé " + created);
            return created;
        }

        @Override
        public ActeDTO updateActe(ActeDTO dto) {
            return dto;
        }

        @Override
        public void deleteActe(Long id) {
            actes.removeIf(a -> a.id().equals(id));
            System.out.println("MOCK: Acte supprimé " + id);
        }

        @Override
        public List<ActeDTO> getAllActes() {
            return new ArrayList<>(actes);
        }

        @Override
        public MedicamentDTO createMedicament(MedicamentDTO dto) {
            long newId = medicaments.size() + 1L;
            MedicamentDTO created = new MedicamentDTO(newId, dto.nom(), dto.laboratoire(), dto.type(), dto.forme(),
                    dto.remboursable(), dto.prixUnitaire(), dto.description());
            medicaments.add(created);
            System.out.println("MOCK: Médicament créé " + created);
            return created;
        }

        @Override
        public MedicamentDTO updateMedicament(MedicamentDTO dto) {
            return dto;
        }

        @Override
        public void deleteMedicament(Long id) {
            medicaments.removeIf(m -> m.id().equals(id));
            System.out.println("MOCK: Médicament supprimé " + id);
        }

        @Override
        public List<MedicamentDTO> getAllMedicaments() {
            return new ArrayList<>(medicaments);
        }

        // --- NEW METHODS ---

        @Override
        public List<Assurance> getAllAssurances() {
            return Arrays.asList(Assurance.values());
        }

        @Override
        public RefAntecedentDTO createRefAntecedent(RefAntecedentDTO dto) {
            long newId = antecedents.size() + 1L;
            RefAntecedentDTO created = new RefAntecedentDTO(newId, dto.nom(), dto.categorie(), dto.risque());
            antecedents.add(created);
            System.out.println("MOCK: Antécédent créé " + created);
            return created;
        }

        @Override
        public void deleteRefAntecedent(Long id) {
            antecedents.removeIf(a -> a.id().equals(id));
            System.out.println("MOCK: Antécédent supprimé " + id);
        }

        @Override
        public List<RefAntecedentDTO> getAllRefAntecedents() {
            return new ArrayList<>(antecedents);
        }
    }
}
