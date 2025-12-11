
-- 1. ROLES
INSERT INTO role (id, libelle) VALUES
(1, 'ADMIN'),
(2, 'MEDECIN'),
(3, 'SECRETAIRE')
ON DUPLICATE KEY UPDATE libelle=VALUES(libelle);

-- 2. CABINET MEDICAL
INSERT INTO cabinet_medical (id, nom, adresse, telephone1, email) VALUES
(1, 'DentalTech Cabinet', 'EMSI-Rabat', '0537-000001', 'contact@dentaltech.ma')
ON DUPLICATE KEY UPDATE nom=VALUES(nom);

-- 3. UTILISATEURS
INSERT INTO utilisateur (id, nom, prenom, email, login, mot_de_passe, role_id) VALUES
(1, 'Admin', 'Global', 'admin@dentalTech.ma', 'admin', '$2a$10$wK1yF07z5/B9J0xQ.U/t9.u.D1t2kQyM.Q/F1A0g9O/W.o.i.i.i.', 1), -- ADMIN
(2, 'BERDAY', 'Aya', 'medecin.aya@dentalTech.ma', 'medecin', '$2a$10$wK1yF07z5/B9J0xQ.U/t9.u.D1t2kQyM.Q/F1A0g9O/W.o.i.i.i.', 2), -- MEDECIN
(3, 'ELBEKALI', 'Aicha', 'secretaire.aicha@dentalTech.ma', 'secretaire', '$2a$10$wK1yF07z5/B9J0xQ.U/t9.u.D1t2kQyM.Q/F1A0g9O/W.o.i.i.i.', 3) -- SECRETAIRE
ON DUPLICATE KEY UPDATE mot_de_passe=VALUES(mot_de_passe);

-- 4. STAFF
INSERT INTO staff (id, salaire, date_recrutement, cabinet_id) VALUES
(2, 20000.00, '2023-01-15', 1),
(3, 6000.00, '2024-05-01', 1)
ON DUPLICATE KEY UPDATE salaire=VALUES(salaire);

-- 5. MEDECIN & SECRETAIRE
INSERT INTO medecin (id, specialite) VALUES
(2, 'Chirurgie Dentaire')
ON DUPLICATE KEY UPDATE specialite=VALUES(specialite);

INSERT INTO secretaire (id, num_cnss) VALUES
(3, '1234567890')
ON DUPLICATE KEY UPDATE num_cnss=VALUES(num_cnss);

-- 6. PATIENT (Ajout de quelques patients de test)
INSERT INTO patient (id, nom, prenom, date_naissance, sexe, telephone, assurance, date_creation) VALUES
(1, 'El Fassi', 'Omar', '1990-05-10', 'H', '0655-112233', 'CNOPS', NOW()),
(2, 'Alaoui', 'Sanae', '1985-11-20', 'F', '0655-445566', 'CNSS', NOW() - INTERVAL 1 DAY),
(3, 'Benali', 'Youssef', '2000-01-01', 'H', '0655-778899', 'AUCUNE', NOW());
ON DUPLICATE KEY UPDATE nom=VALUES(nom);

-- 7. ACTES (Ajout d'actes dentaires de base)
INSERT INTO acte (id, libelle, categorie, prix_base) VALUES
(101, 'Détartrage', 'Hygiène', 300.00),
(102, 'Extraction Molaire', 'Chirurgie', 800.00),
(103, 'Plombage Simple', 'Restauration', 550.00);
ON DUPLICATE KEY UPDATE prix_base=VALUES(prix_base);

-- 8. AUTRES DONNÉES DE TEST (Pour le module Caisse/Dashboard)

INSERT INTO dossier_medical (id, patient_id, medecin_id) VALUES (1, 1, 2);
INSERT INTO consultation (id, dossier_id, date_consultation, statut) VALUES (1, 1, NOW(), 'TERMINE');

-- 8.2 Facture (associée à la Consultation 1)
INSERT INTO facture (id, consultation_id, date_facture, total_facture, total_paye, statut) VALUES
(1, 1, CURRENT_DATE(), 550.00, 300.00, 'PARTIEL'); -- Reste 250.00

-- 8.3 Charges et Revenus (pour le mois actuel)
INSERT INTO charge (id, cabinet_id, titre, description, montant, date_charge) VALUES
(1, 1, 'Loyer Mensuel', 'Paiement du loyer du cabinet', 8000.00, NOW() - INTERVAL 5 DAY),
(2, 1, 'Salaires Staff', 'Salaires de la secrétaire et du médecin', 26000.00, NOW() - INTERVAL 2 DAY);

INSERT INTO revenu (id, cabinet_id, titre, description, montant, date_revenu) VALUES
(1, 1, 'Vente de produits', 'Vente de brosses et dentifrices', 500.00, NOW() - INTERVAL 1 DAY);

-- 9. MEDICAMENTS (Module Ordonnance)
INSERT INTO medicament (id, nom, laboratoire, type_medicament, forme, remboursable, prix_unitaire, description, date_creation)
VALUES
(1, 'Doliprane 1000mg', 'Sanofi', 'Antalgique', 'COMPRIME', 1, 12.50, 'Douleurs modérées', NOW()),
(2, 'Amoxicilline 1g', 'Sandoz', 'Antibiotique', 'COMPRIME', 1, 28.00, 'Infections ORL', NOW())
ON DUPLICATE KEY UPDATE prix_unitaire = VALUES(prix_unitaire);
-- 10. ORDONNANCE pour la consultation 1
INSERT INTO ordonnance (id, dossier_id, consultation_id, date_ordo, date_creation)
VALUES
(1, 1, 1, CURRENT_DATE(), NOW())
ON DUPLICATE KEY UPDATE date_ordo = VALUES(date_ordo);
-- 11. PRESCRIPTIONS liées à l'ordonnance 1
INSERT INTO prescription (id, ordonnance_id, medicament_id, quantite, frequence, duree_en_jours, date_creation)
VALUES
(1, 1, 1, 1, '1 comprimé matin et soir', 5, NOW()),
(2, 1, 2, 2, '1 comprimé matin', 7, NOW())
ON DUPLICATE KEY UPDATE frequence = VALUES(frequence);
-- 12. CERTIFICAT pour le dossier 1
INSERT INTO certificat (id, dossier_id, date_debut, date_fin, duree, note_medecin, date_creation)
VALUES
(1, 1, CURRENT_DATE(), CURRENT_DATE() + INTERVAL 3 DAY, 3,
 'Arrêt de travail suite à intervention dentaire',
 NOW())
ON DUPLICATE KEY UPDATE duree = VALUES(duree);
-- 13. INTERVENTION MEDECIN (Acte sur la consultation 1)
INSERT INTO intervention_medecin (id, consultation_id, acte_id, prix_patient, num_dent, date_creation)
VALUES
(1, 1, 101, 300.00, 26, NOW())
ON DUPLICATE KEY UPDATE prix_patient = VALUES(prix_patient);
-- 14. NOTIFICATIONS (pour l'admin et la secrétaire)
INSERT INTO notification (id, utilisateur_id, titre, message, priorite,
                          date_notification, date_creation)
VALUES
(1, 1, 'Sauvegarde système', 'Sauvegarde quotidienne terminée avec succès.', 'MOYENNE', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),
(2, 3, 'Rappel RDV', 'Vous avez 5 rendez-vous à confirmer aujourd''hui.', 'HAUTE', NOW(), NOW())
ON DUPLICATE KEY UPDATE message = VALUES(message);

