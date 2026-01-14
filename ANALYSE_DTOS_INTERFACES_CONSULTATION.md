# 📋 Analyse des DTOs nécessaires pour les Interfaces Consultation

## 🎯 Interfaces à développer

### 1. Interface "Mes consultations" (ConsultationPagePanel)
**État** : Existe déjà, mais il faut ajouter le bouton "+ Ajouter une consultation"

**Colonnes du tableau** :
- Nom du patient
- Date
- Facture (€XX)
- Statut (Planifié, En cours, Annulé, Terminé)
- Actions (Consulter, Modifier, Supprimer)

**DTOs utilisés** :
- ✅ `ConsultationListItemDTO` - Pour afficher les lignes du tableau
- ✅ `ConsultationListRequestDTO` - Pour les filtres (patient, date, statut)

### 2. Formulaire Modal "Ajouter une consultation" (ConsultationAddFormUI)
**État** : À créer

**Champs du formulaire** :
1. **Dossier médical du patient** (dropdown/combobox)
   - Afficher : "Nom du patient - Dossier #ID"
   - Valeur : dossierId
   - ❌ DTO manquant : `DossierSelectDTO` ou similaire

2. **Date de consultation** (date picker)
   - Type : LocalDate
   - ✅ DTO existant : `ConsultationDTO.date`

3. **Statut** (dropdown)
   - Options : PLANIFIE, EN_COURS, TERMINE, ANNULE
   - ✅ DTO existant : `ConsultationDTO.statut`

4. **Observation du médecin** (textarea)
   - Type : String
   - ✅ DTO existant : `ConsultationDTO.observationMedecin`

**DTOs nécessaires pour le formulaire** :
- ✅ `ConsultationDTO` - Contient: id, dossierId, date, statut, observationMedecin
- ✅ `SaveConsultationRequestDTO` - Contient: ConsultationDTO consultation, ActorDTO actor
- ❌ **DTO manquant pour dropdown dossier** : `DossierSelectDTO` (id, patientNomComplet)

## 📦 DTOs existants

### ✅ ConsultationListItemDTO
```java
- consultationId
- dossierId
- patientId
- patientNomComplet ✅
- dateConsultation
- statut
- factureId
- totalFacture
```
**Utilisation** : Affichage des lignes du tableau

### ✅ ConsultationListRequestDTO
```java
- medecinId
- patientKeyword
- date
- statut
- dateFrom, dateTo
- page, size, sortBy, sortDir
```
**Utilisation** : Filtres de recherche

### ✅ ConsultationDTO
```java
- id
- dossierId ✅
- date ✅
- statut ✅
- observationMedecin ✅
```
**Utilisation** : Création/Modification d'une consultation

### ✅ SaveConsultationRequestDTO
```java
- ConsultationDTO consultation
- ActorDTO actor
```
**Utilisation** : Requête pour créer/modifier (service layer)

### ✅ ActorDTO
```java
- username
```
**Utilisation** : Identifiant de l'utilisateur qui effectue l'action

## ❌ DTOs manquants

### DossierSelectDTO (pour le dropdown)
**Nécessité** : Pour afficher les dossiers médicaux dans un dropdown avec le nom du patient

**Structure proposée** :
```java
public record DossierSelectDTO(
    Long dossierId,
    Long patientId,
    String patientNomComplet  // "Nom Prénom"
) {}
```

**Alternative** : Utiliser `DossierListItemDTO` mais il manque `patientNomComplet`

## 🔄 Workflow des DTOs

### Flux de création d'une consultation :

1. **UI → Controller** :
   - Formulaire remplit → `ConsultationDTO` (sans id, avec dossierId, date, statut, observationMedecin)
   - + `ActorDTO` (username du médecin connecté)
   - = `SaveConsultationRequestDTO`

2. **Controller → Service** :
   - Passe `SaveConsultationRequestDTO` tel quel

3. **Service → Repository** :
   - Convertit `ConsultationDTO` → `Consultation` (Entity)
   - Appelle `repository.create(consultation)`

4. **Service → Controller** :
   - Retourne `LongResponseDTO` (id de la consultation créée)

5. **Controller → UI** :
   - Retourne l'id (ou void si succès)
   - UI rafraîchit la liste

## 📝 Actions à faire

### 1. Créer DTO pour dropdown dossier médical
- Option A : Créer `DossierSelectDTO` avec patientNomComplet
- Option B : Créer méthode dans service/repository qui retourne une liste jointe
- Option C : Modifier `DossierListItemDTO` pour inclure patientNomComplet

**Recommandation** : Option C - Modifier `DossierListItemDTO` ou créer un nouveau DTO spécifique pour le select

### 2. Ajouter méthode create() dans ConsultationController
```java
void create(ConsultationDTO consultation, String username);
// ou
Long create(SaveConsultationRequestDTO request);
```

### 3. Créer ConsultationAddFormUI
- Dialog modal
- Champs selon la maquette
- Utilise DTOs existants + nouveau DTO pour dropdown

### 4. Connecter le tout
- Bouton "+ Ajouter" → Ouvre ConsultationAddFormUI
- Formulaire → Appelle Controller.create()
- Succès → Ferme dialog + Rafraîchit la liste
