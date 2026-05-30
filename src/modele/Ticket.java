package modele;

import java.time.LocalDate;

public class Ticket {
    private Long id;
    private String titre;
    private String description;
    private Utilisateur createur;
    private LocalDate date;
    private String location;
    private Statut statut;

    public Ticket(Long id, String titre, String description, Utilisateur createur, LocalDate date, String location) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.createur = createur;
        this.date = date;
        this.location = location;
        this.statut = Statut.EN_COURS;
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Utilisateur getCreateur() { return createur; }
    public LocalDate getDate() { return date; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
}
