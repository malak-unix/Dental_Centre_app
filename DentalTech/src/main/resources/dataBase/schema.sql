DROP DATABASE IF EXISTS dentalsoft_db;
CREATE DATABASE dentalsoft_db;
USE dentalsoft_db;

SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
--  ROLE
-- =========================================================
CREATE TABLE role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  libelle ENUM('ADMIN','MEDECIN','SECRETAIRE') NOT NULL,
  privileges VARCHAR(255),
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100)
);

-- =========================================================
--  UTILISATEUR  (BaseEntity)
-- =========================================================
CREATE TABLE utilisateur (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  adresse VARCHAR(255),
  cin VARCHAR(32) UNIQUE,
  tel VARCHAR(40),
  sexe ENUM('HOMME','FEMME','AUTRE') DEFAULT 'AUTRE',
  login VARCHAR(64) NOT NULL UNIQUE,
  mot_de_passe VARCHAR(255) NOT NULL,
  date_naissance DATE,
  last_login DATETIME,
  actif BOOLEAN NOT NULL DEFAULT TRUE,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  role_id BIGINT,
  CONSTRAINT fk_utilisateur_role
    FOREIGN KEY (role_id) REFERENCES role(id)
    ON DELETE SET NULL
);

CREATE INDEX idx_utilisateur_role ON utilisateur(role_id);

-- =========================================================
--  CABINET MEDICAL  (BaseEntity)
-- =========================================================
CREATE TABLE cabinet_medical (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(150) NOT NULL,
  nom_medecin VARCHAR(150),
  logo VARCHAR(255),
  adresse VARCHAR(255),
  telephone1 VARCHAR(30),
  telephone2 VARCHAR(30),
  site_web VARCHAR(150),
  instagram VARCHAR(100),
  facebook VARCHAR(100),
  email VARCHAR(150),
  slogan VARCHAR(255),
  description TEXT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100)
);

-- =========================================================
--  STAFF  (hérite de Utilisateur dans UML)
-- =========================================================
CREATE TABLE staff (
  id BIGINT PRIMARY KEY,
  salaire DECIMAL(12,2) DEFAULT 0,
  prime DECIMAL(12,2) DEFAULT 0,
  date_recrutement DATE,
  solde_conge INT DEFAULT 0,
  cabinet_id BIGINT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_staff_utilisateur
    FOREIGN KEY (id) REFERENCES utilisateur(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_staff_cabinet
    FOREIGN KEY (cabinet_id) REFERENCES cabinet_medical(id)
    ON DELETE SET NULL
);

-- =========================================================
--  MEDECIN  (hérite de Staff)
-- =========================================================
CREATE TABLE medecin (
  id BIGINT PRIMARY KEY,
  specialite VARCHAR(150),
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_medecin_staff
    FOREIGN KEY (id) REFERENCES staff(id)
    ON DELETE CASCADE
);

-- =========================================================
--  SECRETAIRE  (hérite de Staff)
-- =========================================================
CREATE TABLE secretaire (
  id BIGINT PRIMARY KEY,
  num_cnss VARCHAR(100),
  commission DECIMAL(8,2) DEFAULT 0,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_secretaire_staff
    FOREIGN KEY (id) REFERENCES staff(id)
    ON DELETE CASCADE
);

-- =========================================================
--  AGENDA MENSUEL  (BaseEntity, 1–1 avec Médecin)
-- =========================================================
CREATE TABLE agenda_mensuel (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  medecin_id BIGINT NOT NULL,
  mois ENUM('JANVIER','FEVRIER','MARS','AVRIL','MAI','JUIN',
            'JUILLET','AOUT','SEPTEMBRE','OCTOBRE','NOVEMBRE','DECEMBRE') NOT NULL,
  annee INT NOT NULL,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_agenda_medecin
    FOREIGN KEY (medecin_id) REFERENCES medecin(id)
    ON DELETE CASCADE,
  CONSTRAINT uc_agenda_medecin UNIQUE (medecin_id, mois, annee)
);

-- =========================================================
--  DETAIL JOURNEE  (composition de AgendaMensuel)
-- =========================================================
CREATE TABLE detail_journee (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  agenda_id BIGINT NOT NULL,
  date_jour DATE NOT NULL,
  heure_debut_travail TIME,
  heure_fin_travail TIME,
  etat_jour ENUM('OUVERT','FERME','FERIE','VACANCES') DEFAULT 'OUVERT',
  commentaire VARCHAR(255),
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_detail_agenda
    FOREIGN KEY (agenda_id) REFERENCES agenda_mensuel(id)
    ON DELETE CASCADE         -- composition : suppression des journées si agenda supprimé
);

-- =========================================================
--  PATIENT  (BaseEntity)
-- =========================================================
CREATE TABLE patient (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  date_naissance DATE,
  sexe ENUM('H','F') DEFAULT 'H',
  telephone VARCHAR(40),
  adresse VARCHAR(255),
  num_affiliation VARCHAR(50),
  etat_civil ENUM('CELIBATAIRE','MARIE','DIVORCE','VEUF') DEFAULT 'CELIBATAIRE',
  assurance ENUM('CNSS','CNOPS','MUTUELLE','AUTRE','AUCUNE') DEFAULT 'AUCUNE',
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100)
);

-- =========================================================
--  ANTECEDENT  (BaseEntity)
-- =========================================================
CREATE TABLE antecedent (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT NOT NULL,
  nom VARCHAR(255),
  categorie VARCHAR(150),
  niveau_de_risque ENUM('FAIBLE','MOYEN','ELEVE') DEFAULT 'MOYEN',
  description TEXT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_antecedent_patient
    FOREIGN KEY (patient_id) REFERENCES patient(id)
    ON DELETE CASCADE
);

-- =========================================================
--  DOSSIER MEDICAL  (BaseEntity, agrégation de Patient)
-- =========================================================
CREATE TABLE dossier_medical (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT NOT NULL,
  medecin_id BIGINT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  notes TEXT,
  CONSTRAINT fk_dossier_patient
    FOREIGN KEY (patient_id) REFERENCES patient(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_dossier_medecin
    FOREIGN KEY (medecin_id) REFERENCES medecin(id)
    ON DELETE SET NULL
);

-- =========================================================
--  SITUATION FINANCIERE  (BaseEntity, composition avec Dossier)
-- =========================================================
CREATE TABLE situation_financiere (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dossier_id BIGINT NOT NULL UNIQUE,
  medecin_id BIGINT,
  total_des_actes DECIMAL(12,2) DEFAULT 0,
  total_paye DECIMAL(12,2) DEFAULT 0,
  credit DECIMAL(12,2) DEFAULT 0,
  statut ENUM('NORMAL','EN_CREANCE','EN_PROMO') DEFAULT 'NORMAL',
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_sitfin_dossier
    FOREIGN KEY (dossier_id) REFERENCES dossier_medical(id)
    ON DELETE CASCADE,      -- composition
  CONSTRAINT fk_sitfin_medecin
    FOREIGN KEY (medecin_id) REFERENCES medecin(id)
    ON DELETE SET NULL
);

-- =========================================================
--  ACTE  (BaseEntity)
-- =========================================================
CREATE TABLE acte (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  libelle VARCHAR(200) NOT NULL,
  categorie VARCHAR(150),
  prix_base DECIMAL(10,2) DEFAULT 0,
  description TEXT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100)
);

-- =========================================================
--  CONSULTATION  (BaseEntity, associée au Dossier)
-- =========================================================
CREATE TABLE consultation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dossier_id BIGINT NOT NULL,
  date_consultation DATETIME NOT NULL,
  statut ENUM('PLANIFIE','TERMINE','ANNULE') DEFAULT 'PLANIFIE',
  observation_medecin TEXT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_consultation_dossier
    FOREIGN KEY (dossier_id) REFERENCES dossier_medical(id)
    ON DELETE CASCADE
);

-- =========================================================
--  INTERVENTION MEDECIN  (BaseEntity, composition de Consultation)
-- =========================================================
CREATE TABLE intervention_medecin (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  consultation_id BIGINT NOT NULL,
  acte_id BIGINT,
  prix_patient DECIMAL(10,2) DEFAULT 0,
  num_dent INT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_interv_consultation
    FOREIGN KEY (consultation_id) REFERENCES consultation(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_interv_acte
    FOREIGN KEY (acte_id) REFERENCES acte(id)
    ON DELETE SET NULL
);

-- =========================================================
--  LISTE D'ATTENTE  (BaseEntity, agrège des RDV)
-- =========================================================
CREATE TABLE liste_attente (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(150),
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100)
);

-- =========================================================
--  RDV  (BaseEntity, composition avec Dossier, agrégation ListeAttente)
-- =========================================================
CREATE TABLE rdv (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id BIGINT,
  detail_journee_id BIGINT,
  liste_attente_id BIGINT,
  date_rdv DATE NOT NULL,
  heure TIME,
  motif VARCHAR(255),
  statut ENUM('PLANIFIE','ANNULE','TERMINE') DEFAULT 'PLANIFIE',
  note_medecin TEXT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_rdv_patient
    FOREIGN KEY (patient_id) REFERENCES patient(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_rdv_detail_journee
    FOREIGN KEY (detail_journee_id) REFERENCES detail_journee(id)
    ON DELETE SET NULL,
  CONSTRAINT fk_rdv_liste_attente
    FOREIGN KEY (liste_attente_id) REFERENCES liste_attente(id)
    ON DELETE SET NULL
);

-- =========================================================
--  ORDONNANCE  (BaseEntity)
-- =========================================================
CREATE TABLE ordonnance (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dossier_id BIGINT,
  consultation_id BIGINT,
  date_ordo DATE NOT NULL,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_ordonnance_dossier
    FOREIGN KEY (dossier_id) REFERENCES dossier_medical(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_ordonnance_consultation
    FOREIGN KEY (consultation_id) REFERENCES consultation(id)
    ON DELETE CASCADE
);

-- =========================================================
--  MEDICAMENT  (BaseEntity)
-- =========================================================
CREATE TABLE medicament (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(200) NOT NULL,
  laboratoire VARCHAR(150),
  type_medicament VARCHAR(100),
  forme ENUM('COMPRIME','SIROP','GEL','INJECTION','AUTRE') DEFAULT 'COMPRIME',
  remboursable TINYINT(1) DEFAULT 0,
  prix_unitaire DECIMAL(10,2) DEFAULT 0,
  description TEXT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100)
);

-- =========================================================
--  PRESCRIPTION  (BaseEntity, composition avec Ordonnance)
-- =========================================================
CREATE TABLE prescription (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  ordonnance_id BIGINT NOT NULL,
  medicament_id BIGINT,
  quantite INT DEFAULT 1,
  frequence VARCHAR(100),
  duree_en_jours INT DEFAULT 0,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_prescription_ordonnance
    FOREIGN KEY (ordonnance_id) REFERENCES ordonnance(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_prescription_medicament
    FOREIGN KEY (medicament_id) REFERENCES medicament(id)
    ON DELETE SET NULL
);

-- =========================================================
--  CERTIFICAT  (BaseEntity)
-- =========================================================
CREATE TABLE certificat (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dossier_id BIGINT,
  date_debut DATE,
  date_fin DATE,
  duree INT,
  note_medecin TEXT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_certif_dossier
    FOREIGN KEY (dossier_id) REFERENCES dossier_medical(id)
    ON DELETE CASCADE
);

-- =========================================================
--  FACTURE  (BaseEntity, composition avec Dossier)
-- =========================================================
CREATE TABLE facture (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  consultation_id BIGINT,
  date_facture DATE NOT NULL,
  total_facture DECIMAL(12,2) DEFAULT 0,
  total_paye DECIMAL(12,2) DEFAULT 0,
  reste DECIMAL(12,2) AS (total_facture - total_paye) STORED,
  statut ENUM('NON_PAYEE','PARTIEL','PAYEE') DEFAULT 'NON_PAYEE',
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_facture_consultation
    FOREIGN KEY (consultation_id) REFERENCES consultation(id)
    ON DELETE SET NULL
);

-- =========================================================
--  REVENUS  (BaseEntity, agrégation Cabinet)
-- =========================================================
CREATE TABLE revenu (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cabinet_id BIGINT NOT NULL,
  titre VARCHAR(200),
  description TEXT,
  montant DECIMAL(12,2) NOT NULL,
  date_revenu DATETIME,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_revenu_cabinet
    FOREIGN KEY (cabinet_id) REFERENCES cabinet_medical(id)
    ON DELETE CASCADE
);

-- =========================================================
--  CHARGES  (BaseEntity, agrégation Cabinet)
-- =========================================================
CREATE TABLE charge (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cabinet_id BIGINT NOT NULL,
  titre VARCHAR(200),
  description TEXT,
  montant DECIMAL(12,2) NOT NULL,
  date_charge DATETIME,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_charge_cabinet
    FOREIGN KEY (cabinet_id) REFERENCES cabinet_medical(id)
    ON DELETE CASCADE
);

-- =========================================================
--  STATISTIQUE  (BaseEntity, agrégation Cabinet)
-- =========================================================
CREATE TABLE statistique (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cabinet_id BIGINT NOT NULL,
  nom VARCHAR(150),
  categorie ENUM('FINANCIER','ACTIVITE','AUTRE') DEFAULT 'AUTRE',
  chiffre DECIMAL(14,2) DEFAULT 0,
  date_calcul DATE,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_stat_cabinet
    FOREIGN KEY (cabinet_id) REFERENCES cabinet_medical(id)
    ON DELETE CASCADE
);

-- =========================================================
--  NOTIFICATION  (BaseEntity)
-- =========================================================
CREATE TABLE notification (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  utilisateur_id BIGINT NOT NULL,
  titre VARCHAR(200),
  message TEXT NOT NULL,
  date_notification DATETIME DEFAULT CURRENT_TIMESTAMP,
  priorite ENUM('HAUTE','MOYENNE','FAIBLE') DEFAULT 'MOYENNE',
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_notif_utilisateur
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
    ON DELETE CASCADE
);

-- =========================================================
--  LOGS  (BaseEntity)
-- =========================================================
CREATE TABLE logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  utilisateur_id BIGINT,
  date_log DATETIME DEFAULT CURRENT_TIMESTAMP,
  entite_attribue VARCHAR(100),
  action VARCHAR(100),
  description TEXT,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_logs_utilisateur
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
    ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS = 1;