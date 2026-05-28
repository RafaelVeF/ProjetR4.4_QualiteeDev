package gestionincidents.dao;

import gestionincidents.model.Utilisateur;
import java.util.List;

public interface UtilisateurDao {
    Utilisateur sauvegarder(Utilisateur utilisateur);
    Utilisateur trouverParId(Long id);
    Utilisateur trouverParEmail(String email);
    List<Utilisateur> trouverTous();
    void supprimer(Long id);
}
