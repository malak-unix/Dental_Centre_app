USE dentalsoft_db;

SET FOREIGN_KEY_CHECKS = 0;


--  ROLES (ADMIN / MEDECIN / SECRETAIRE)

INSERT INTO role (id, libelle, privileges, cree_par)
VALUES
  (1, 'ADMIN',     'ALL',                     'system'),
  (2, 'MEDECIN',   'CONSULTATION,CAISSE',     'system'),
  (3, 'SECRETAIRE','AGENDA,CAISSE,FACTURATION','system');

--  CABINET MEDICAL

INSERT INTO cabinet_medical (id, nom, nom_medecin, logo, adresse, telephone1, telephone2,
                             site_web, instagram, facebook, email, slogan, description, cree_par)
VALUES
  (1,
   'Cabinet Dentaire Anfa Smile',
   'Dr. Jihane El Idrissi',
   NULL,
   'Rabat Hassan',
   '+212522001122',
   '+212661223344',
   'www.anfasmile.ma',
   'anfasmile_dental',
   'fb.com/anfasmile',
   'contact@anfasmile.ma',
   'Un sourire sain pour une vie sereine',
   'Cabinet dentaire moderne au cœur de Rabat',
   'system');

--  UTILISATEURS
INSERT INTO utilisateur (id, nom, prenom, email, adresse, cin, tel, sexe,
                         login, mot_de_passe, date_naissance, actif,
                         role_id, cree_par)
VALUES
  (1, 'Admin',   'Tech',   'admin@dentalsoft.ma', 'Casablanca', 'X000000', '+212600000000', 'AUTRE',
   'admin',   '$2b$10$A3a0ymwfAYPc8kXV9ll.0OtokztmxGQ8N.j8v7i6wtKxMnCKcIUgm', '1990-01-01', 1, 1, 'system'),

  (2, 'Achari',  'Malak',  'malak@dentalsoft.ma', 'yaacoub mansour, Rabat', 'J123456', '+212612345601', 'FEMME',
   'malak',  '$2b$10$IkKDhgjaJS9WHyU3pu0BEeqOUHlrpyjphklwdWJNAkWzZh.I3Ze4.', '2002-03-14', 1, 3, 'admin'),

  (3, 'Berday',  'Aya',    'aya@dentalsoft.ma', 'Mehdia, Kenitra', 'J654321', '+212612345602', 'FEMME',
   'aya',    '$2b$10$IkKDhgjaJS9WHyU3pu0BEeqOUHlrpyjphklwdWJNAkWzZh.I3Ze4.', '2002-09-21', 1, 3, 'admin'),

  (4, 'El bekali','Aicha', 'aicha@dentalsoft.ma', 'massira, Temara', 'J789456', '+212612345603', 'FEMME',
   'aicha',  '$2b$10$IkKDhgjaJS9WHyU3pu0BEeqOUHlrpyjphklwdWJNAkWzZh.I3Ze4.', '2001-12-05', 1, 3, 'admin'),

  (5, 'Ou-tamssout','Jihane','jihane@dentalsoft.ma','Agdal, Rabat','J147258', '+212612345604','FEMME',
   'drjihane','$2b$10$IkKDhgjaJS9WHyU3pu0BEeqOUHlrpyjphklwdWJNAkWzZh.I3Ze4.', '1988-06-10', 1, 2, 'admin');

    (6, 'El Idrissi', 'Imane', 'imane@dentalsoft.ma', 'Rabat', 'J999111', '+212612300006', 'FEMME',
      'drimane', '$2b$10$IkKDhgjaJS9WHyU3pu0BEeqOUHlrpyjphklwdWJNAkWzZh.I3Ze4.', '1989-02-10', 1, 2, 'admin'),

     (7, 'Berrada', 'Yassine', 'yassine@dentalsoft.ma', 'Rabat', 'J999222', '+212612300007', 'HOMME',
      'dryassine', '$2b$10$IkKDhgjaJS9WHyU3pu0BEeqOUHlrpyjphklwdWJNAkWzZh.I3Ze4.', '1985-07-22', 1, 2, 'admin');
--  STAFF

INSERT INTO staff (id, salaire, prime, date_recrutement, solde_conge,
                   cabinet_id, cree_par)
VALUES
  (1, 12000.00,  0.00, '2020-01-01', 10, 1, 'admin'),   -- Admin technique
  (2,  6000.00, 500.00, '2024-01-15', 12, 1, 'admin'),  -- Malak (secrétaire)
  (3,  5800.00, 300.00, '2024-02-10', 10, 1, 'admin'),  -- Aya (secrétaire)
  (4,  5800.00, 300.00, '2024-02-10', 10, 1, 'admin'),  -- Aicha (secrétaire)
  (5, 15000.00,1000.00, '2018-09-01', 15, 1, 'admin');  -- Dr Jihane (médecin)
  (6, 14000.00, 800.00, '2019-01-01', 12, 1, 'admin'), --DR El idrisse imane (medecin)
  (7, 16000.00,1000.00, '2017-03-01', 15, 1, 'admin'); --DR Berrada Yassine ( medecin)

--  MEDECIN & SECRETAIRES

INSERT INTO medecin (id, specialite, cree_par)
VALUES
  (5, 'Chirurgie dentaire', 'admin');
   (6, 'Orthodontie', 'admin'),
    (7, 'Endodontie',  'admin');

INSERT INTO secretaire (id, num_cnss, commission, cree_par)
VALUES
  (2, 'CNSS-2024-001', 5.00, 'admin'),
  (3, 'CNSS-2024-002', 5.00, 'admin'),
  (4, 'CNSS-2024-003', 5.00, 'admin');


--  AGENDA MENSUEL + DETAILS JOURNÉE

INSERT INTO agenda_mensuel (id, medecin_id, mois, annee, cree_par)
VALUES
  (1, 5, 'JANVIER', 2025, 'aya'),
  (2, 5, 'FEVRIER', 2025, 'aya');

INSERT INTO detail_journee (id, agenda_id, date_jour,
                            heure_debut_travail, heure_fin_travail,
                            etat_jour, commentaire, cree_par)
VALUES
  (1, 1, '2025-01-15', '09:00:00', '17:00:00', 'OUVERT', 'Journée normale', 'aya'),
  (2, 1, '2025-01-16', '09:00:00', '13:00:00', 'OUVERT', 'Matinée uniquement', 'aya');


--  PATIENTS

INSERT INTO patient (id, nom, prenom, date_naissance, sexe,
                     telephone, adresse, num_affiliation, etat_civil,
                     assurance, cree_par)
VALUES
  (1, 'El Fassi',    'Youssef', '1995-04-12', 'H', '+212661111111',
   'Salé, Bettana', 'CNSS-001', 'CELIBATAIRE', 'CNSS', 'aya'),

  (2, 'Benkirane',   'Salma',   '1992-07-23', 'F', '+212662222222',
   'Rabat, Hay Ryad', 'CNSS-002','MARIE', 'MUTUELLE', 'malak'),

  (3, 'Mansouri',    'Omar',    '1988-11-03', 'H', '+212663333333',
   'Casablanca, Sidi Maarouf', 'CNOPS-001', 'MARIE', 'CNOPS', 'aicha');

--  ANTECEDENTS

INSERT INTO antecedent (patient_id, nom, categorie, niveau_de_risque,
                        description, cree_par)
VALUES
  (1, 'Hypertension artérielle', 'Cardio', 'MOYEN', 'Sous traitement', 'drjihane'),
  (2, 'Diabète type 2', 'Métabolique', 'ELEVE', 'Contrôle glycémie régulier', 'drjihane'),
  (3, 'Allergie à la pénicilline', 'Allergie', 'ELEVE', 'À noter avant prescription', 'drjihane');


--  DOSSIERS MÉDICAUX

INSERT INTO dossier_medical (id, patient_id, medecin_id, notes, cree_par)
VALUES
  (1, 1, 5, 'Première consultation pour douleur molaire gauche.', 'aya'),
  (2, 2, 5, 'Suivi orthodontique.', 'aya'),
  (3, 3, 5, 'Contrôle annuel.', 'aya');


--  SITUATION FINANCIÈRE (module CAISSE)
INSERT INTO situation_financiere (id, dossier_id, medecin_id,
                                  total_des_actes, total_paye, credit,
                                  statut, cree_par)
VALUES
  (1, 1, 5, 1200.00, 800.00, 400.00, 'EN_CREANCE', 'malak'),
  (2, 2, 5,  800.00, 800.00,   0.00, 'NORMAL',    'malak'),
  (3, 3, 5,  300.00, 300.00,   0.00, 'NORMAL',    'malak');

--  ACTES DENTAIRES
INSERT INTO acte (id, libelle, categorie, prix_base, description, cree_par)
VALUES
  (1, 'Détartrage complet', 'Hygiène', 300.00, 'Nettoyage complet des dents', 'drjihane'),
  (2, 'Extraction molaire', 'Chirurgie', 700.00, 'Extraction d''une molaire cariée', 'drjihane'),
  (3, 'Panoramique dentaire', 'Imagerie', 200.00, 'Radio panoramique', 'drjihane');

--  CONSULTATIONS
INSERT INTO consultation (id, dossier_id, date_consultation, statut,
                          observation_medecin, cree_par)
VALUES
  (1, 1, '2025-01-15 10:30:00', 'TERMINE', 'Douleur molaire, recommandation extraction', 'aya'),
  (2, 2, '2025-01-15 11:30:00', 'TERMINE', 'Contrôle appareil orthodontique', 'aya'),
  (3, 3, '2025-01-16 10:00:00', 'PLANIFIE', 'Contrôle annuel programmé', 'aya');

--  INTERVENTIONS DU MEDECIN
INSERT INTO intervention_medecin (consultation_id, acte_id, prix_patient,
                                  num_dent, cree_par)
VALUES
  (1, 2, 700.00, 36, 'drjihane'),
  (1, 1, 300.00, 0,  'drjihane'),
  (2, 1, 300.00, 0,  'drjihane');

--  LISTE D'ATTENTE & RDV
INSERT INTO liste_attente (id, nom, cree_par)
VALUES
  (1, 'Liste du matin', 'aya');

INSERT INTO rdv (id, patient_id, detail_journee_id, liste_attente_id,
                 date_rdv, heure, motif, statut, note_medecin, cree_par)
VALUES
  (1, 1, 1, 1, '2025-01-15', '10:30:00', 'Douleur molaire', 'TERMINE', 'Voir radio panoramique', 'aya'),
  (2, 2, 1, 1, '2025-01-15', '11:30:00', 'Suivi appareil', 'TERMINE', 'Tout est stable', 'aya'),
  (3, 3, 2, NULL, '2025-01-16', '10:00:00', 'Contrôle annuel', 'PLANIFIE', NULL, 'aya');

--  ORDONNANCES, MEDICAMENTS, PRESCRIPTIONS (simple)
INSERT INTO medicament (id, nom, laboratoire, type_medicament, forme,
                        remboursable, prix_unitaire, description, cree_par)
VALUES
  (1, 'Doliprane 1000mg', 'Sanofi', 'Antalgique', 'COMPRIME', 0, 20.00, 'Douleurs modérées à intenses', 'drjihane'),
  (2, 'Amoxicilline 1g', 'GSK', 'Antibiotique', 'COMPRIME', 0, 35.00, 'Traitement infection dentaire', 'drjihane');

INSERT INTO ordonnance (id, dossier_id, consultation_id, date_ordo, cree_par)
VALUES
  (1, 1, 1, '2025-01-15', 'drjihane');

INSERT INTO prescription (ordonnance_id, medicament_id, quantite, frequence,
                          duree_en_jours, cree_par)
VALUES
  (1, 1, 10, '1 comprimé si douleur', 5, 'drjihane'),
  (1, 2, 14, '1 comprimé 2x/jour',    7, 'drjihane');

--  CERTIFICATS
INSERT INTO certificat (dossier_id, date_debut, date_fin, duree,
                        note_medecin, cree_par)
VALUES
  (1, '2025-01-15', '2025-01-17', 3, 'Repos après extraction molaire.', 'drjihane');

--  FACTURES (Module CAISSE)
INSERT INTO facture (id, consultation_id, date_facture,
                     total_facture, total_paye, statut, cree_par)
VALUES
  (1, 1, '2025-01-15', 1000.00, 600.00, 'PARTIEL',   'malak'),
  (2, 2, '2025-01-15',  300.00, 300.00, 'PAYEE',     'malak'),
  (3, 3, '2025-01-16',  400.00,   0.00, 'NON_PAYEE', 'malak');

--  REVENUS (hors factures)
INSERT INTO revenu (id, cabinet_id, titre, description, montant,
                    date_revenu, cree_par)
VALUES
  (1, 1, 'Vente de kit de blanchiment', 'Kit blanchiment à domicile', 800.00,
   '2025-01-10 15:00:00', 'aya'),
  (2, 1, 'Formation interne', 'Participation à une formation payante', 1200.00,
   '2025-01-12 10:00:00', 'malak');

--  CHARGES DU CABINET
INSERT INTO charge (id, cabinet_id, titre, description, montant,
                    date_charge, cree_par)
VALUES
  (1, 1, 'Loyer cabinet', 'Loyer mensuel du local', 8000.00,
   '2025-01-01 09:00:00', 'malak'),
  (2, 1, 'Achat matériel', 'Gants, masques, compresses', 1500.00,
   '2025-01-08 11:00:00', 'aya'),
  (3, 1, 'Facture électricité', 'Facture électricité décembre', 900.00,
   '2025-01-05 10:30:00', 'aya');

--  STATISTIQUES (exemple pour Admin)
INSERT INTO statistique (cabinet_id, nom, categorie, chiffre, date_calcul, cree_par)
VALUES
  (1, 'CA Janvier 2025',       'FINANCIER', 15000.00, '2025-01-31', 'admin'),
  (1, 'Nombre de patients 2025','ACTIVITE',    3.00, '2025-01-31', 'admin');

--  NOTIFICATIONS
INSERT INTO notification (utilisateur_id, titre, message, priorite, cree_par)
VALUES
  (2, 'Facture partiellement réglée',
   'Le patient Youssef El Fassi a un reste à payer de 400 MAD.', 'MOYENNE', 'system'),
  (3, 'RDV aujourd''hui',
   'Vous avez 3 rendez-vous prévus aujourd''hui.', 'FAIBLE', 'system'),
  (5, 'Contrôle annuel',
   'Pensez à relancer les patients en contrôle annuel.', 'HAUTE', 'system');

--  LOGS
INSERT INTO logs (utilisateur_id, entite_attribue, action, description, cree_par)
VALUES
  (2, 'facture', 'CREATION', 'Création facture n°1 pour dossier 1', 'system'),
  (3, 'rdv',     'PLANIFICATION', 'Planification RDV patient Salma', 'system'),
  (5, 'consultation', 'MISE_A_JOUR', 'Mise à jour consultation patient Omar', 'system');

--  PLAGES HORAIRES (pour RDV)
INSERT INTO plage_horaire (detail_journee_id, heure_debut, heure_fin,
                           disponible, cree_par)
VALUES
  (1, '09:00:00', '09:30:00', 0, 'aya'),
  (1, '09:30:00', '10:00:00', 0, 'malak'),
  (1, '10:00:00', '10:30:00', 0, 'aya'),
  (1, '10:30:00', '11:00:00', 0, 'aicha'),
  (1, '11:00:00', '11:30:00', 1, 'aya');

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO document_medical
(dossier_id, consultation_id, type_document, titre, nom_fichier, chemin_fichier, taille_octets, date_document, cree_par, modifie_par)
VALUES
(1, 1, 'ANALYSE', 'Analyse sanguine', 'analyse_2025_01.pdf',
 'C:/dentaltech/uploads/analyse_2025_01.pdf', 204800, NOW(), 'SEED', 'SEED');

