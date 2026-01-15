🦷 DentalTech
Application Desktop de Gestion d’un Cabinet Dentaire

Projet académique – EMSI Rabat
Réalisé par une équipe d’étudiants
Encadré par Pr. Omar El Midaoui

📖 Sommaire

Présentation générale

Objectifs du projet

Technologies utilisées

Architecture globale

Gestion des rôles et accès

Fonctionnalités par module

Base de données

Structure du projet

Interface utilisateur (UI/UX)

Installation et exécution

Scénario de démonstration

État d’avancement

Conclusion

🩺 Présentation générale

DentalTech est une application desktop Java Swing destinée à la gestion complète d’un cabinet dentaire.
Elle permet de gérer :

les patients,

les dossiers médicaux,

les consultations,

les rendez-vous,

la caisse,

les ordonnances et certificats,

ainsi que l’administration globale du cabinet.

L’application est conçue selon une architecture MVC en couches, avec une séparation claire entre :

UI (Swing),

Controllers,

Services,

Repositories (JDBC),

Entités métiers.

🎯 Objectifs du projet

Centraliser la gestion d’un cabinet dentaire

Automatiser les tâches administratives et médicales

Séparer clairement les responsabilités (MVC)

Gérer les accès par rôles utilisateurs

Fournir une interface claire, moderne et cohérente

Respecter les bonnes pratiques (SOLID, faible couplage)

🧰 Technologies utilisées
Catégorie	Technologie
Langage	Java SE
UI	Java Swing
Base de données	MySQL 8
Accès BD	JDBC
Build	Maven
Utilitaires	Lombok
IDE	IntelliJ IDEA
Méthodologie	MVC + DAO + Services
🏗️ Architecture globale

Le projet respecte une architecture en couches :

ma.dentalTech
│
├── configuration        → ApplicationContext, SessionFactory
├── entities             → Entités métiers (Patient, Medicament, Facture…)
├── repository           → DAO JDBC (interfaces + impl)
├── service              → Logique métier
├── mvc
│   ├── controllers      → Controllers par module
│   ├── dto              → DTO (Auth, Dashboard, etc.)
│   └── ui
│       ├── common       → Composants UI réutilisables
│       ├── modules
│       │   ├── admin
│       │   ├── agenda
│       │   ├── caisse
│       │   ├── dashboard
│       │   ├── dossierMedicale
│       │   ├── patient
│       │   └── auth
└── common               → Exceptions, utilitaires


👉 Chaque couche ne dépend que de la couche inférieure.

👥 Gestion des rôles et accès

L’application gère 3 rôles principaux :

🛠️ Administrateur (ADMIN)

Gestion des utilisateurs

Gestion des rôles

Gestion des actes

Gestion des médicaments

Gestion globale des antécédents

Sauvegarde / restauration

Statistiques globales

🧾 Secrétaire (SECRETAIRE)

Gestion des patients

Gestion des rendez-vous

Planning

Liste d’attente

Caisse (factures, paiements)

Dashboard secrétaire

🩺 Médecin (MEDECIN)

Accès aux patients

Dossiers médicaux

Consultations

Ordonnances

Certificats

Situation financière

Dashboard médecin

👉 Les menus sont dynamiquement générés via RoleMenuConfig.

🧩 Fonctionnalités par module
🔐 Authentification

Login sécurisé

Vérification mot de passe (BCrypt)

Chargement du rôle et des privilèges

Redirection vers le dashboard approprié

📊 Dashboard

Un dashboard unique dont le contenu change selon le rôle

Indicateurs :

patients

rendez-vous

caisse

activités récentes

statistiques

👤 Patients

CRUD patient

Recherche

Consultation des antécédents

Lien avec dossiers médicaux

📁 Dossier médical

Dossiers par patient

Filtrage par médecin

Accès secrétaire / médecin

🩺 Consultations

Création et suivi

Lien avec actes

Lien avec ordonnances et certificats

💊 Médicaments

Gestion ADMIN

Consultation via ordonnances

🧾 Ordonnances

Création depuis consultation

Liste des prescriptions

Médicaments associés

📄 Certificats

Génération par médecin

Liés au dossier médical

💰 Caisse

Factures

Paiements

Totaux (payé / impayé)

Filtres par date

📅 Agenda & Liste d’attente

Planning médecin

RDV

File d’attente

🗄️ Base de données

Schema : schema.sql

Données de test complètes : seed.sql

Contient :

utilisateurs

rôles

patients

dossiers

consultations

factures

médicaments

ordonnances

certificats

statistiques

Exemple configuration (db.properties)
db.url=jdbc:mysql://localhost:3306/dentalsoft_db
db.user=root
db.password=

📁 Structure des ressources
src/main/resources
├── assets
│   ├── icons
│   │   ├── dashboard.png
│   │   ├── patients.png
│   │   ├── caisse.png
│   │   ├── planning.png
│   │   └── ...
│   └── logo.png
├── db.properties
├── schema.sql
└── seed.sql

🎨 Interface utilisateur (UI/UX)

Thème cohérent (DentalTheme)

Sidebar dynamique selon rôle

Header commun :

recherche

utilisateur

bouton déconnexion

Icônes PNG homogènes

Layouts propres et responsifs (Swing)

⚙️ Installation et exécution
1️⃣ Cloner le projet
git clone https://github.com/malak-unix/Dental_Centre_app.git
cd Dental_Centre_app/DentalTech

2️⃣ Base de données

Créer une base dentalsoft_db

Exécuter :

schema.sql

seed.sql

3️⃣ Configurer la connexion

Modifier :

src/main/resources/db.properties

4️⃣ Lancer l’application

Depuis IntelliJ :

Lancer MainApp ou LoginFrame

Ou via Maven :

mvn clean install

🧪 Scénario de démonstration

Connexion en secrétaire

Consultation dashboard

Accès patients

Caisse (factures)

Déconnexion

Connexion en médecin

Consultations / ordonnances

Connexion en admin

Gestion médicaments / utilisateurs

🚧 État d’avancement

✅ Architecture complète
✅ Authentification
✅ UI cohérente
✅ Seed complet pour tests
🟡 Certains modules encore extensibles (statistiques avancées, exports)

🏁 Conclusion

DentalTech est une application desktop robuste, modulaire et évolutive, respectant les bonnes pratiques de développement Java et les exigences pédagogiques du projet.

Elle constitue une base solide pour :

une extension future,

une migration web,

ou une utilisation réelle en cabinet.