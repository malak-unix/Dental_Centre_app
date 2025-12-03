package ma.dentalTech.entities.enums;

public enum TypeNotification {
    RENDEZ_VOUS,       // rappel, confirmation, annulation
    FACTURE,           // facture créée, en retard, payée
    PAIEMENT,          // paiement reçu, échec de paiement
    DOCUMENT_MEDICAL,  // certificat, ordonnance prête
    INFORMATION,       // info générale du cabinet
    URGENCE            // alerte urgente
}
