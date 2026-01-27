USE dentalsoft_db;

SET FOREIGN_KEY_CHECKS = 0;

-- Dates dynamiques (semaine en cours)
SET @TODAY = CURDATE();
SET @YESTERDAY = DATE_SUB(@TODAY, INTERVAL 1 DAY);
SET @TOMORROW = DATE_ADD(@TODAY, INTERVAL 1 DAY);
SET @MONDAY = DATE_SUB(@TODAY, INTERVAL WEEKDAY(@TODAY) DAY);
SET @TUESDAY = DATE_ADD(@MONDAY, INTERVAL 1 DAY);
SET @WEDNESDAY = DATE_ADD(@MONDAY, INTERVAL 2 DAY);
SET @THURSDAY = DATE_ADD(@MONDAY, INTERVAL 3 DAY);
SET @FRIDAY = DATE_ADD(@MONDAY, INTERVAL 4 DAY);
SET @NEXT_MONDAY = DATE_ADD(@MONDAY, INTERVAL 7 DAY);
SET @NEXT_TUESDAY = DATE_ADD(@MONDAY, INTERVAL 8 DAY);
SET @NEXT_WEDNESDAY = DATE_ADD(@MONDAY, INTERVAL 9 DAY);
SET @NEXT_THURSDAY = DATE_ADD(@MONDAY, INTERVAL 10 DAY);
SET @NEXT_FRIDAY = DATE_ADD(@MONDAY, INTERVAL 11 DAY);
SET @YEAR = YEAR(@TODAY);
SET @MOIS = CASE MONTH(@TODAY)
  WHEN 1 THEN 'JANVIER'
  WHEN 2 THEN 'FEVRIER'
  WHEN 3 THEN 'MARS'
  WHEN 4 THEN 'AVRIL'
  WHEN 5 THEN 'MAI'
  WHEN 6 THEN 'JUIN'
  WHEN 7 THEN 'JUILLET'
  WHEN 8 THEN 'AOUT'
  WHEN 9 THEN 'SEPTEMBRE'
  WHEN 10 THEN 'OCTOBRE'
  WHEN 11 THEN 'NOVEMBRE'
  ELSE 'DECEMBRE'
END;


--  ROLES (ADMIN / MEDECIN / SECRETAIRE)

INSERT INTO role (id, libelle, privileges, cree_par)
VALUES
  (1, 'ADMIN',     'ALL',                     'system'),
  (2, 'MEDECIN',   'CONSULTATION,CAISSE',     'system'),
  (3, 'SECRETAIRE','AGENDA,CAISSE,FACTURATION','system');

--  CABINET MEDICAL

-- Mot de passe commun : 123456
SET @PWD_BCRYPT = '$2a$10$NuyM2WsHFr0/LMKxlJiP2.RNTuCULl3x9Eu9LrFP7QeEa/NVRZWLq';

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
   'Cabinet dentaire moderne au coeur de Rabat',
   'system');

--  UTILISATEURS
INSERT INTO utilisateur (id, nom, prenom, email, adresse, cin, tel, sexe,
                         login, mot_de_passe, date_naissance, actif,
                         role_id, cree_par)
VALUES
    (1, 'Admin',   'Tech',   'admin@dentalsoft.ma', 'Casablanca', 'X000000', '+212600000000', 'AUTRE',
     'admin',   @PWD_BCRYPT, '1990-01-01', 1, 1, 'system'),

    (2, 'Achari',  'Malak',  'malak@dentalsoft.ma', 'yaacoub mansour, Rabat', 'J123456', '+212612345601', 'FEMME',
     'malak',  @PWD_BCRYPT, '2002-03-14', 1, 3, 'admin'),

    (3, 'Berday',  'Aya',    'aya@dentalsoft.ma', 'Mehdia, Kenitra', 'J654321', '+212612345602', 'FEMME',
     'aya',    @PWD_BCRYPT, '2002-09-21', 1, 3, 'admin'),

    (4, 'El bekali','Aicha', 'aicha@dentalsoft.ma', 'massira, Temara', 'J789456', '+212612345603', 'FEMME',
     'aicha',  @PWD_BCRYPT, '2001-12-05', 1, 3, 'admin'),

    (5, 'Ou-tamssout','Jihane','jihane@dentalsoft.ma','Agdal, Rabat','J147258', '+212612345604','FEMME',
     'drjihane', @PWD_BCRYPT, '1988-06-10', 1, 2, 'admin'),

    (6, 'El Idrissi', 'Imane', 'imane@dentalsoft.ma', 'Rabat', 'J999111', '+212612300006', 'FEMME',
     'drimane', @PWD_BCRYPT, '1989-02-10', 1, 2, 'admin'),

    (7, 'Berrada', 'Yassine', 'yassine@dentalsoft.ma', 'Rabat', 'J999222', '+212612300007', 'HOMME',
     'dryassine', @PWD_BCRYPT, '1985-07-22', 1, 2, 'admin');

--  STAFF
INSERT INTO staff (id, salaire, prime, date_recrutement, solde_conge,
                   cabinet_id, cree_par)
VALUES
    (1, 12000.00,  0.00, '2020-01-01', 10, 1, 'admin'),
    (2,  6000.00, 500.00, '2024-01-15', 12, 1, 'admin'),
    (3,  5800.00, 300.00, '2024-02-10', 10, 1, 'admin'),
    (4,  5800.00, 300.00, '2024-02-10', 10, 1, 'admin'),
    (5, 15000.00,1000.00, '2018-09-01', 15, 1, 'admin'),
    (6, 14000.00, 800.00, '2019-01-01', 12, 1, 'admin'),
    (7, 16000.00,1000.00, '2017-03-01', 15, 1, 'admin');

--  MEDECIN & SECRETAIRES

INSERT INTO medecin (id, specialite, cree_par)
VALUES
    (5, 'Chirurgie dentaire', 'admin'),
    (6, 'Orthodontie', 'admin'),    (7, 'Endodontie',  'admin');

INSERT INTO secretaire (id, num_cnss, commission, cree_par)
VALUES
  (2, 'CNSS-2024-001', 5.00, 'admin'),
  (3, 'CNSS-2024-002', 5.00, 'admin'),
  (4, 'CNSS-2024-003', 5.00, 'admin');


--  AGENDA MENSUEL + DETAILS JOURNEE

INSERT INTO agenda_mensuel (id, medecin_id, mois, annee, cree_par)
VALUES
  (1, 5, @MOIS, @YEAR, 'aya'),
  (2, 6, @MOIS, @YEAR, 'aya'),
  (3, 7, @MOIS, @YEAR, 'aya');

INSERT INTO detail_journee (id, agenda_id, date_jour,
                            heure_debut_travail, heure_fin_travail,
                            etat_jour, commentaire, cree_par)
VALUES
  (1, 1, @MONDAY, '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'aya'),
  (2, 1, @TUESDAY, '09:00:00', '13:00:00', 'OUVERT', 'Matinee uniquement', 'aya');

--  DETAILS JOURNEE (SEMAINE COURANTE + SEMAINE PROCHAINE) - TOUS LES MEDECINS
INSERT INTO detail_journee (id, agenda_id, date_jour,
                            heure_debut_travail, heure_fin_travail,
                            etat_jour, commentaire, cree_par)
VALUES
  -- Medecin 5 (agenda_id = 1) : semaine courante
  (3, 1, @WEDNESDAY, '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (4, 1, @THURSDAY,  '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (5, 1, @FRIDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  -- Medecin 5 : semaine prochaine
  (6, 1, @NEXT_MONDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (7, 1, @NEXT_TUESDAY,   '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (8, 1, @NEXT_WEDNESDAY, '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (9, 1, @NEXT_THURSDAY,  '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (10, 1, @NEXT_FRIDAY,   '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),

  -- Medecin 6 (agenda_id = 2) : semaine courante
  (11, 2, @MONDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (12, 2, @TUESDAY,   '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (13, 2, @WEDNESDAY, '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (14, 2, @THURSDAY,  '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (15, 2, @FRIDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  -- Medecin 6 : semaine prochaine
  (16, 2, @NEXT_MONDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (17, 2, @NEXT_TUESDAY,   '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (18, 2, @NEXT_WEDNESDAY, '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (19, 2, @NEXT_THURSDAY,  '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (20, 2, @NEXT_FRIDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),

  -- Medecin 7 (agenda_id = 3) : semaine courante
  (21, 3, @MONDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (22, 3, @TUESDAY,   '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (23, 3, @WEDNESDAY, '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (24, 3, @THURSDAY,  '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (25, 3, @FRIDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  -- Medecin 7 : semaine prochaine
  (26, 3, @NEXT_MONDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (27, 3, @NEXT_TUESDAY,   '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (28, 3, @NEXT_WEDNESDAY, '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (29, 3, @NEXT_THURSDAY,  '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed'),
  (30, 3, @NEXT_FRIDAY,    '09:00:00', '17:00:00', 'OUVERT', 'Journee normale', 'seed');


--  PATIENTS

INSERT INTO patient (id, nom, prenom, date_naissance, sexe,
                     telephone, adresse, num_affiliation, etat_civil,
                     assurance, cree_par)
VALUES
  (1, 'El Fassi',    'Youssef', '1995-04-12', 'H', '+212661111111',
   'Sale, Bettana', 'CNSS-001', 'CELIBATAIRE', 'CNSS', 'aya'),

  (2, 'Benkirane',   'Salma',   '1992-07-23', 'F', '+212662222222',
   'Rabat, Hay Ryad', 'CNSS-002','MARIE', 'MUTUELLE', 'malak'),

  (3, 'Mansouri',    'Omar',    '1988-11-03', 'H', '+212663333333',
   'Casablanca, Sidi Maarouf', 'CNOPS-001', 'MARIE', 'CNOPS', 'aicha');

--  ANTECEDENTS

INSERT INTO antecedent (patient_id, nom, categorie, niveau_de_risque,
                        description, cree_par)
VALUES
  (1, 'Hypertension arterielle', 'Cardio', 'MOYEN', 'Sous traitement', 'drjihane'),
  (2, 'Diabete type 2', 'Metabolique', 'ELEVE', 'Controle glycemie regulier', 'drjihane'),
  (3, 'Allergie a la penicilline', 'Allergie', 'ELEVE', 'A noter avant prescription', 'drjihane');


--  DOSSIERS MEDICAUX

INSERT INTO dossier_medical (id, patient_id, medecin_id, notes, cree_par)
VALUES
  (1, 1, 5, 'Premiere consultation pour douleur molaire gauche.', 'aya'),
  (2, 2, 5, 'Suivi orthodontique.', 'aya'),
  (3, 3, 5, 'Controle annuel.', 'aya');


--  SITUATION FINANCIERE (module CAISSE)
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
  (1, 'Detartrage complet', 'Hygiene', 300.00, 'Nettoyage complet des dents', 'drjihane'),
  (2, 'Extraction molaire', 'Chirurgie', 700.00, 'Extraction d''une molaire cariee', 'drjihane'),
  (3, 'Panoramique dentaire', 'Imagerie', 200.00, 'Radio panoramique', 'drjihane');

--  CONSULTATIONS
INSERT INTO consultation (id, dossier_id, date_consultation, statut,
                          observation_medecin, cree_par)
VALUES
  (1, 1, CONCAT(@TODAY, ' 10:30:00'), 'TERMINE', 'Douleur molaire, recommandation extraction', 'aya'),
  (2, 2, CONCAT(@TODAY, ' 11:30:00'), 'TERMINE', 'Controle appareil orthodontique', 'aya'),
  (3, 3, CONCAT(@TOMORROW, ' 10:00:00'), 'PLANIFIE', 'Controle annuel programme', 'aya');

--  INTERVENTIONS DU MEDECIN
INSERT INTO intervention_medecin (consultation_id, acte_id, prix_patient,
                                  num_dent, cree_par)
VALUES
  (1, 2, 700.00, 36, 'drjihane'),
  (1, 1, 300.00, 0,  'drjihane'),
  (2, 1, 300.00, 0,  'drjihane');

--  LISTE D'ATTENTE & RDV
INSERT INTO liste_attente (id, patient_id, nom, motif, date_ajout, priorite, cree_par)
VALUES
  (1, 1, 'Liste du matin', 'Douleur molaire', NOW(), 'NORMALE', 'aya');

INSERT INTO rdv (id, patient_id, detail_journee_id, liste_attente_id,
                 date_rdv, heure, motif, statut, note_medecin, cree_par)
VALUES
  (1, 1, 1, 1, @TODAY, '10:30:00', 'Douleur molaire', 'TERMINE', 'Voir radio panoramique', 'aya'),
  (2, 2, 1, 1, @TODAY, '11:30:00', 'Suivi appareil', 'TERMINE', 'Tout est stable', 'aya'),
  (3, 3, 2, NULL, @TOMORROW, '10:00:00', 'Controle annuel', 'PLANIFIE', NULL, 'aya');

-- RDV supplementaires (tous les medecins, semaine courante + prochaine)
INSERT INTO rdv (id, patient_id, detail_journee_id, liste_attente_id,
                 date_rdv, heure, motif, statut, note_medecin, cree_par)
SELECT
  10 + (id - 3) AS id,
  CASE (id % 3) WHEN 0 THEN 1 WHEN 1 THEN 2 ELSE 3 END AS patient_id,
  id AS detail_journee_id,
  NULL AS liste_attente_id,
  date_jour AS date_rdv,
  '09:00:00' AS heure,
  'Consultation controle' AS motif,
  CASE (id % 4)
    WHEN 0 THEN 'PLANIFIE'
    WHEN 1 THEN 'CONFIRME'
    WHEN 2 THEN 'TERMINE'
    ELSE 'ANNULE'
  END AS statut,
  NULL AS note_medecin,
  'seed' AS cree_par
FROM detail_journee
WHERE id BETWEEN 3 AND 30;

--  ORDONNANCES, MEDICAMENTS, PRESCRIPTIONS (simple)
INSERT INTO medicament (id, nom, laboratoire, type_medicament, forme,
                        remboursable, prix_unitaire, description, cree_par)
VALUES
  (1, 'Doliprane 1000mg', 'Sanofi', 'Antalgique', 'COMPRIME', 0, 20.00, 'Douleurs moderees a intenses', 'drjihane'),
  (2, 'Amoxicilline 1g', 'GSK', 'Antibiotique', 'COMPRIME', 0, 35.00, 'Traitement infection dentaire', 'drjihane'),
  (3, 'Ibuprofene 400mg', 'Biopharma', 'Anti-inflammatoire', 'COMPRIME', 0, 18.00, 'Douleur et inflammation', 'drjihane'),
  (4, 'Metronidazole 500mg', 'Cooper', 'Antibiotique', 'COMPRIME', 0, 28.00, 'Infection anaerobie dentaire', 'drjihane'),
  (5, 'Augmentin 1g', 'GSK', 'Antibiotique', 'COMPRIME', 0, 55.00, 'Infections bucco-dentaires', 'drjihane'),
  (6, 'Chlorhexidine 0.12%', 'Gaba', 'Antiseptique', 'SOLUTION', 0, 30.00, 'Bain de bouche antiseptique', 'drjihane'),
  (7, 'Lidocaine 2%', 'Astra', 'Anesthesique', 'INJECTION', 0, 45.00, 'Anesthesie locale', 'drjihane'),
  (8, 'Prednisone 20mg', 'Sanofi', 'Corticoide', 'COMPRIME', 0, 26.00, 'Anti-inflammatoire', 'drjihane'),
  (9, 'Ketoprofene 100mg', 'Menarini', 'Anti-inflammatoire', 'COMPRIME', 0, 22.00, 'Douleur post-operatoire', 'drjihane'),
  (10, 'Benzocaine gel', 'Septodont', 'Anesthesique', 'GEL', 0, 38.00, 'Douleur gingivale', 'drjihane'),
  (11, 'Amoxicilline 500mg', 'GSK', 'Antibiotique', 'COMPRIME', 0, 25.00, 'Infections bucco-dentaires', 'drjihane'),
  (12, 'Spiramycin Metronidazole', 'Sanofi', 'Antibiotique', 'COMPRIME', 0, 42.00, 'Infections parodontales', 'drjihane');

INSERT INTO ordonnance (id, dossier_id, consultation_id, date_ordo, cree_par)
VALUES
  (1, 1, 1, @TODAY, 'drjihane');

INSERT INTO prescription (ordonnance_id, medicament_id, quantite, frequence,
                          duree_en_jours, cree_par)
VALUES
  (1, 1, 10, '1 comprime si douleur', 5, 'drjihane'),
  (1, 2, 14, '1 comprime 2x/jour',    7, 'drjihane');

--  CERTIFICATS
INSERT INTO certificat (dossier_id, date_debut, date_fin, duree,
                        note_medecin, cree_par)
VALUES
  (1, @TODAY, DATE_ADD(@TODAY, INTERVAL 2 DAY), 3, 'Repos apres extraction molaire.', 'drjihane');

--  FACTURES (Module CAISSE)
INSERT INTO facture (id, consultation_id, date_facture,
                     total_facture, total_paye, reste, statut, cree_par)
VALUES
  (1, 1, @TODAY, 1000.00, 600.00, 400.00, 'PARTIEL',   'malak'),
  (2, 2, @TODAY,  300.00, 300.00,   0.00, 'PAYEE',     'malak'),
  (3, 3, @TOMORROW,  400.00,   0.00, 400.00, 'NON_PAYEE', 'malak');

--  REVENUS (hors factures)
INSERT INTO revenu (id, cabinet_id, titre, description, montant,
                    date_revenu, cree_par)
VALUES
  (1, 1, 'Vente de kit de blanchiment', 'Kit blanchiment a domicile', 800.00,
   CONCAT(@YESTERDAY, ' 15:00:00'), 'aya'),
  (2, 1, 'Formation interne', 'Participation a une formation payante', 1200.00,
   CONCAT(@TODAY, ' 10:00:00'), 'malak');

--  CHARGES DU CABINET
INSERT INTO charge (id, cabinet_id, titre, description, montant,
                    date_charge, cree_par)
VALUES
  (1, 1, 'Loyer cabinet', 'Loyer mensuel du local', 8000.00,
   CONCAT(@MONDAY, ' 09:00:00'), 'malak'),
  (2, 1, 'Achat materiel', 'Gants, masques, compresses', 1500.00,
   CONCAT(@TUESDAY, ' 11:00:00'), 'aya'),
  (3, 1, 'Facture electricite', 'Facture electricite decembre', 900.00,
   CONCAT(@WEDNESDAY, ' 10:30:00'), 'aya');

--  STATISTIQUES (exemple pour Admin)
INSERT INTO statistique (cabinet_id, nom, categorie, chiffre, date_calcul, cree_par)
VALUES
  (1, 'CA semaine courante',       'FINANCIER', 15000.00, @TODAY, 'admin'),
  (1, 'Nombre de patients',        'ACTIVITE',    3.00, @TODAY, 'admin');

--  NOTIFICATIONS
INSERT INTO notification (utilisateur_id, titre, message, priorite, cree_par)
VALUES
  (2, 'Facture partiellement reglee',
   'Le patient Youssef El Fassi a un reste a payer de 400 MAD.', 'MOYENNE', 'system'),
  (3, 'RDV aujourd''hui',
   'Vous avez 3 rendez-vous prevus aujourd''hui.', 'FAIBLE', 'system'),
  (5, 'Controle annuel',
   'Pensez a relancer les patients en controle annuel.', 'HAUTE', 'system');

--  LOGS
INSERT INTO logs (utilisateur_id, entite_attribue, action, description, cree_par)
VALUES
  (2, 'facture', 'CREATION', 'Creation facture n1 pour dossier 1', 'system'),
  (3, 'rdv',     'PLANIFICATION', 'Planification RDV patient Salma', 'system'),
  (5, 'consultation', 'MISE_A_JOUR', 'Mise a jour consultation patient Omar', 'system');

--  PLAGES HORAIRES (pour RDV)
INSERT INTO plage_horaire (detail_journee_id, heure_debut, heure_fin,
                           disponible, cree_par)
VALUES
  (1, '09:00:00', '09:30:00', 0, 'aya'),
  (1, '09:30:00', '10:00:00', 0, 'malak'),
  (1, '10:00:00', '10:30:00', 0, 'aya'),
  (1, '10:30:00', '11:00:00', 0, 'aicha'),
  (1, '11:00:00', '11:30:00', 1, 'aya');

--  PLAGES HORAIRES (tous les details journees, semaine courante + prochaine)
INSERT INTO plage_horaire (detail_journee_id, heure_debut, heure_fin, disponible, cree_par)
SELECT id, '09:00:00', '09:30:00', 0, 'seed'
FROM detail_journee
WHERE id BETWEEN 2 AND 30;

INSERT INTO plage_horaire (detail_journee_id, heure_debut, heure_fin, disponible, cree_par)
SELECT id, '09:30:00', '10:00:00', 1, 'seed'
FROM detail_journee
WHERE id BETWEEN 2 AND 30;

INSERT INTO plage_horaire (detail_journee_id, heure_debut, heure_fin, disponible, cree_par)
SELECT id, '10:00:00', '10:30:00', 1, 'seed'
FROM detail_journee
WHERE id BETWEEN 2 AND 30;

UPDATE plage_horaire
SET disponible = 0
WHERE detail_journee_id = 2
  AND heure_debut = '10:00:00';

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO document_medical
(dossier_id, consultation_id, type_document, titre, nom_fichier, chemin_fichier, taille_octets, date_document, cree_par, modifie_par)
VALUES
(1, 1, 'ANALYSE', 'Analyse sanguine', 'analyse_2025_01.pdf',
 'C:/dentaltech/uploads/analyse_2025_01.pdf', 204800, NOW(), 'SEED', 'SEED');
