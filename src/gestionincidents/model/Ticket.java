package gestionincidents.model;

public class Ticket {
    private Long id;
    private String titre;
    private String description;
    private Statut statut;
    private Utilisateur createur;

    public Ticket(Long id, String titre, String description, Utilisateur createur) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.statut = statut.NOUVEAU;
        this.createur = createur;
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
}
