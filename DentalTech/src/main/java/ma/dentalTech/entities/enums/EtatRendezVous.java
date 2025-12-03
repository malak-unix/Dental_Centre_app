package ma.dentalTech.entities.enums;

public enum EtatRendezVous {
    PREVU,        // créé mais pas encore confirmé
    CONFIRME,     // confirmé par le patient
    EN_COURS,     // patient dans le cabinet
    TERMINE,      // consultation faite
    ANNULE,       // annulé avant l’heure
    ABSENT        // no-show
}
