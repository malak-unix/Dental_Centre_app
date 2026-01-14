package ma.dentalTech.mvc.dto.dossierMedicale.consultation;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ma.dentalTech.entities.enums.StatutConsultation;

public class ConsultationListRequestDTO {
    private Long medecinId; //interface pour un medecin specifique -> consultation qui concerne ce medecin !
    private String patientKeyword;              // "amina", "ben amar"...
    private LocalDate date;                     // recherche par date exacte (optionnel)
    private StatutConsultation statut;          // PLANIFIE / EN_COURS / ANNULE /TERMINE(optionnel)

    private LocalDate dateFrom;                 // filtre intervalle
    private LocalDate dateTo;                   // filtre intervalle

    private Integer page;                       // pagination (optionnel)
    private Integer size;                       // pagination (optionnel)
    private String sortBy;  //filter par "date", "patient", "facture"...
    private String sortDir;                     // "asc" / "desc"

    //GETTERS, SETTERS
    public Long getMedecinId(){return medecinId;}
    public void setMedecinId(Long medecinId) { this.medecinId = medecinId; }

    public StatutConsultation getStatut() { return statut; }
    public void setStatut(StatutConsultation statut) { this.statut = statut; }

    public String getPatientKeyword() { return patientKeyword; }
    public void setPatientKeyword(String patientKeyword) { this.patientKeyword = patientKeyword; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }


}

