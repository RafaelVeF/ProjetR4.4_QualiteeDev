package gestionincidents.model;

import java.time.LocalDate;
import java.util.Date;

public class Ticket {
    private Long id;
    private String titre;
    private String description;
    private Statut statut;
    private Utilisateur createur;
    private LocalDate date;
    private String location;

    public Ticket(Long id, String titre, String description, Utilisateur createur,LocalDate date,String location) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.statut = statut.NOUVEAU;
        this.createur = createur;
        this.date = date;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public LocalDate getDate(){return date;}

    public void setDate(LocalDate date){this.date = date;}

    public String getLocation(){return location;}

    public void setLocation(String location){this.location = location;}
}
