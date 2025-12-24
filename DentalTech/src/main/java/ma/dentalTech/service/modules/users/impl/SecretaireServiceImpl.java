package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.secretaire.Secretaire;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;
import ma.dentalTech.repository.modules.users.impl.SecretaireRepositoryImpl;
import ma.dentalTech.service.modules.users.api.SecretaireService; // Import de l'interface

import java.util.List;

public class SecretaireServiceImpl implements SecretaireService {

    private final SecretaireRepository secretaireRepo = new SecretaireRepositoryImpl();

    @Override
    public List<Secretaire> getAllSecretaires() {
        return secretaireRepo.findAll();
    }

    @Override
    public Secretaire getSecretaireParId(Long id) {
        Secretaire s = secretaireRepo.findById(id);
        if (s == null) {
            throw new RuntimeException("Erreur : Secrétaire introuvable.");
        }
        return s;
    }

    @Override
    public void recruterSecretaire(Secretaire s) {
        // --- VALIDATION METIER ---

        // 1. Vérifier les infos obligatoires
        if (s.getNom() == null || s.getNom().isEmpty()) {
            throw new RuntimeException("Erreur : Le nom est obligatoire.");
        }
        if (s.getEmail() == null || s.getEmail().isEmpty()) {
            throw new RuntimeException("Erreur : L'email est obligatoire.");
        }

        // 2. Vérifier le salaire (car c'est un Staff)
        // On peut mettre une règle : Salaire minimum
        if (s.getSalaire() != null && s.getSalaire() < 0) {
            throw new RuntimeException("Erreur : Le salaire ne peut pas être négatif.");
        }

        // 3. Appel du Repository (Insertion dans Utilisateur + Staff + Secretaire)
        secretaireRepo.create(s);
        System.out.println("Succès : Secrétaire " + s.getNom() + " recrutée.");
    }

    @Override
    public void supprimerSecretaire(Long id) {
        if (secretaireRepo.findById(id) == null) {
            throw new RuntimeException("Impossible de supprimer : ID introuvable.");
        }
        secretaireRepo.deleteById(id);
    }
}