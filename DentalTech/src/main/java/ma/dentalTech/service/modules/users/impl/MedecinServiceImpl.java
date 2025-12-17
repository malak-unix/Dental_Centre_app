package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.medecin.Medecin;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.repository.modules.users.impl.MedecinRepositoryImpl;
import ma.dentalTech.service.modules.users.api.MedecinService; // Import de l'interface

import java.util.List;
import java.util.ArrayList;

public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepo = new MedecinRepositoryImpl();

    @Override
    public List<Medecin> getAllMedecins() {
        return medecinRepo.findAll();
    }

    @Override
    public Medecin getMedecinParId(Long id) {
        Medecin m = medecinRepo.findById(id);
        if (m == null) {
            throw new RuntimeException("Erreur : Médecin introuvable (ID " + id + ")");
        }
        return m;
    }

    @Override
    public void recruterMedecin(Medecin m) {
        // --- VALIDATION METIER ---

        // 1. Spécialité obligatoire (Spécifique Médecin)
        if (m.getSpecialite() == null || m.getSpecialite().trim().isEmpty()) {
            throw new RuntimeException("Erreur : Un médecin doit avoir une spécialité.");
        }

        // 2. Salaire & Pourcentage (Hérité de Staff ou spécifique)
        if (m.getSalaire() != null && m.getSalaire() < 0) {
            throw new RuntimeException("Erreur : Le salaire ne peut pas être négatif.");
        }

        // 3. Infos Utilisateur (Hérité de Utilisateur)
        if (m.getNom() == null || m.getEmail() == null) {
            throw new RuntimeException("Erreur : Nom et Email sont requis pour le recrutement.");
        }

        // 4. Appel au Repository (Il gère l'insertion dans les 3 tables SQL)
        medecinRepo.create(m);
        System.out.println("Succès : Dr. " + m.getNom() + " (" + m.getSpecialite() + ") a été recruté.");
    }

    @Override
    public void supprimerMedecin(Long id) {
        if (medecinRepo.findById(id) == null) {
            throw new RuntimeException("Impossible de supprimer : ID introuvable.");
        }
        medecinRepo.deleteById(id);
    }

    @Override
    public List<Medecin> getMedecinsParSpecialite(String specialite) {
        // Si ton repo n'a pas cette méthode, on peut filtrer en Java (Solution simple JDBC)
        List<Medecin> tous = medecinRepo.findAll();
        List<Medecin> resultats = new ArrayList<>();

        for (Medecin m : tous) {
            if (m.getSpecialite() != null && m.getSpecialite().equalsIgnoreCase(specialite)) {
                resultats.add(m);
            }
        }
        return resultats;
    }
}