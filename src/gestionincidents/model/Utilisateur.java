package gestionincidents.model;

public class Utilisateur {
    private Long id;
    private String nom;
    private String email;
    private Role role;

    public Utilisateur(Long id, String nom, String email, Role role) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.role=role;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole(){return role;}

    public void setRole(Role role){this.role = role;}
}
