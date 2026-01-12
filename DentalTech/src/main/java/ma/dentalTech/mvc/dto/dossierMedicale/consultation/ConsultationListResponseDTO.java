package ma.dentalTech.mvc.dto.dossierMedicale.consultation;

import java.util.List;

public class ConsultationListResponseDTO {
    private List<ConsultationListItemDTO> items;

    private long total;               // total lignes trouvées (utile si pagination)
    private int page;
    private int size;

    // Optionnel: utile pour afficher des "badges" ou stats rapides dans l’UI
    private long nbPlanifie;
    private long nbEnCours;
    private long nbTermine;
    private long nbAnnule;
}
