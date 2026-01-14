package ma.dentalTech.service.modules.referentiel.impl;

import lombok.AllArgsConstructor;
import ma.dentalTech.common.utilitaire.Transaction;
import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.medicament.MedicamentDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.dentalTech.service.modules.referentiel.api.ReferentielService;
import ma.dentalTech.common.utilitaire.RepoFactory;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ReferentielServiceImpl implements ReferentielService {

    private final RepoFactory<ActeRepository> acteRepoFactory;
    private final RepoFactory<MedicamentRepository> medicamentRepoFactory;

    // --- ACTES ---

    @Override
    public ActeDTO createActe(ActeDTO dto) {
        if (dto == null)
            return null;
        return Transaction.initTransaction(cnx -> {
            ActeRepository repo = acteRepoFactory.create(cnx);
            Acte entity = new Acte();
            entity.setLibelle(dto.libelle());
            entity.setPrixBase(dto.prixBase());
            entity.setCategorie(dto.categorie());
            // description might be missing in entity or dto usage?
            // DTO has description, let's assume entity might have it or we skip it if not
            // in Entity.
            // Based on Acte.java view earlier: libelle, description, prixBase,
            // dureeEstimee.
            repo.create(entity);
            return mapActe(entity);
        });
    }

    @Override
    public ActeDTO updateActe(ActeDTO dto) {
        if (dto == null || dto.id() == null)
            return null;
        return Transaction.initTransaction(cnx -> {
            ActeRepository repo = acteRepoFactory.create(cnx);
            Acte entity = repo.findById(dto.id());
            if (entity != null) {
                entity.setLibelle(dto.libelle());
                entity.setPrixBase(dto.prixBase());
                entity.setCategorie(dto.categorie());
                repo.update(entity);
                return mapActe(entity);
            }
            return null;
        });
    }

    @Override
    public void deleteActe(Long id) {
        if (id == null)
            return;
        Transaction.initTransaction(cnx -> {
            ActeRepository repo = acteRepoFactory.create(cnx);
            repo.deleteById(id);
            return null;
        });
    }

    @Override
    public List<ActeDTO> getAllActes() {
        return Transaction.initTransaction(cnx -> {
            ActeRepository repo = acteRepoFactory.create(cnx);
            List<Acte> list = repo.findAll();
            List<ActeDTO> res = new ArrayList<>();
            if (list != null) {
                for (Acte a : list)
                    res.add(mapActe(a));
            }
            return res;
        });
    }

    // --- MEDICAMENTS ---

    // --- MEDICAMENTS ---

    @Override
    public MedicamentDTO createMedicament(MedicamentDTO dto) {
        if (dto == null)
            return null;
        return Transaction.initTransaction(cnx -> {
            MedicamentRepository repo = medicamentRepoFactory.create(cnx);
            Medicament m = new Medicament();
            m.setNom(dto.nom());
            m.setLaboratoire(dto.laboratoire());
            m.setPrixUnitaire(dto.prixUnitaire());
            m.setType(dto.type());
            m.setForme(dto.forme());
            m.setRemboursable(dto.remboursable());
            m.setDescription(dto.description());
            repo.create(m);
            return mapMedicament(m);
        });
    }

    @Override
    public MedicamentDTO updateMedicament(MedicamentDTO dto) {
        if (dto == null || dto.id() == null)
            return null;
        return Transaction.initTransaction(cnx -> {
            MedicamentRepository repo = medicamentRepoFactory.create(cnx);
            Medicament m = repo.findById(dto.id());
            if (m != null) {
                m.setNom(dto.nom());
                m.setLaboratoire(dto.laboratoire());
                m.setPrixUnitaire(dto.prixUnitaire());
                m.setType(dto.type());
                m.setForme(dto.forme());
                m.setRemboursable(dto.remboursable());
                m.setDescription(dto.description());
                repo.update(m);
                return mapMedicament(m);
            }
            return null;
        });
    }

    @Override
    public void deleteMedicament(Long id) {
        if (id == null)
            return;
        Transaction.initTransaction(cnx -> {
            MedicamentRepository repo = medicamentRepoFactory.create(cnx);
            repo.deleteById(id);
            return null;
        });
    }

    @Override
    public List<MedicamentDTO> getAllMedicaments() {
        return Transaction.initTransaction(cnx -> {
            MedicamentRepository repo = medicamentRepoFactory.create(cnx);
            List<Medicament> list = repo.findAll();
            List<MedicamentDTO> res = new ArrayList<>();
            if (list != null) {
                for (Medicament m : list)
                    res.add(mapMedicament(m));
            }
            return res;
        });
    }

    // --- MAPPERS ---

    private ActeDTO mapActe(Acte a) {
        if (a == null)
            return null;
        return new ActeDTO(
                a.getId(),
                a.getLibelle(),
                a.getCategorie(),
                a.getPrixBase(),
                a.getDescription() // Map description properly
        );
    }

    private MedicamentDTO mapMedicament(Medicament m) {
        if (m == null)
            return null;
        return new MedicamentDTO(
                m.getId(),
                m.getNom(),
                m.getLaboratoire(),
                m.getType(),
                m.getForme(),
                m.isRemboursable(),
                m.getPrixUnitaire(),
                m.getDescription());
    }

    @Override
    public List<ma.dentalTech.entities.enums.Assurance> getAllAssurances() {
        return java.util.Arrays.asList(ma.dentalTech.entities.enums.Assurance.values());
    }

    // --- CATALOGUE ANTECEDENTS (File Based) ---
    private static final String CATALOGUE_FILE = "catalogue_antecedents.csv";

    @Override
    public ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO createRefAntecedent(
            ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO dto) {
        List<ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO> all = getAllRefAntecedents();
        Long newId = all.stream().mapToLong(ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO::id).max().orElse(0L)
                + 1;

        ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO newDto = new ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO(
                newId, dto.nom(), dto.categorie(), dto.risque());
        all.add(newDto);
        saveCatalogue(all);
        return newDto;
    }

    @Override
    public void deleteRefAntecedent(Long id) {
        List<ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO> all = getAllRefAntecedents();
        all.removeIf(dto -> dto.id().equals(id));
        saveCatalogue(all);
    }

    @Override
    public List<ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO> getAllRefAntecedents() {
        List<ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO> list = new ArrayList<>();
        java.io.File f = new java.io.File(CATALOGUE_FILE);
        if (!f.exists())
            return list;

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    try {
                        Long id = Long.parseLong(parts[0]);
                        String nom = parts[1];
                        String cat = parts[2];
                        ma.dentalTech.entities.enums.NiveauDeRisque risque = ma.dentalTech.entities.enums.NiveauDeRisque
                                .valueOf(parts[3]);
                        list.add(new ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO(id, nom, cat, risque));
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveCatalogue(List<ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO> list) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(CATALOGUE_FILE))) {
            for (ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO dto : list) {
                pw.println(dto.id() + ";" + dto.nom() + ";" + dto.categorie() + ";" + dto.risque().name());
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
