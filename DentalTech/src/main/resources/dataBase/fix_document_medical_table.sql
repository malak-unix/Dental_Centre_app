USE dentalsoft_db;

-- =========================================================
-- SCRIPT DE CORRECTION : Créer la table document_medical
-- =========================================================
-- Ce script peut être exécuté manuellement si vous souhaitez
-- activer la fonctionnalité de gestion des documents médicaux.
-- 
-- REMARQUE : L'application fonctionne maintenant même sans cette table,
-- mais vous ne pourrez pas ajouter de documents aux dossiers médicaux.
-- =========================================================

-- Créer la table document_medical si elle n'existe pas
CREATE TABLE IF NOT EXISTS document_medical (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dossier_id BIGINT NOT NULL,
  consultation_id BIGINT NULL,
  type_document ENUM('SCANNER','RADIO','ANALYSE','ORDONNANCE','AUTRE') DEFAULT 'AUTRE',
  titre VARCHAR(200),
  nom_fichier VARCHAR(255),
  chemin_fichier VARCHAR(500) NOT NULL,
  taille_octets BIGINT DEFAULT 0,
  date_document DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
  date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cree_par VARCHAR(100),
  modifie_par VARCHAR(100),
  CONSTRAINT fk_doc_dossier
    FOREIGN KEY (dossier_id) REFERENCES dossier_medical(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_doc_consultation
    FOREIGN KEY (consultation_id) REFERENCES consultation(id)
    ON DELETE SET NULL
);

-- Créer les index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_doc_dossier ON document_medical(dossier_id);
CREATE INDEX IF NOT EXISTS idx_doc_consultation ON document_medical(consultation_id);

-- Vérifier que la table a été créée
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ Table document_medical créée avec succès !'
        ELSE '❌ Erreur : La table n\'a pas été créée'
    END AS Statut
FROM information_schema.tables 
WHERE table_schema = 'dentalsoft_db' 
  AND table_name = 'document_medical';
