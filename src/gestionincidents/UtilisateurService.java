package gestionincidents;

import gestionincidents.dao.UtilisateurDao;
import gestionincidents.model.Utilisateur;
import java.util.List;

public class UtilisateurService {

    private final UtilisateurDao utilisateurDao;

    public UtilisateurService(UtilisateurDao utilisateurDao) {
        this.utilisateurDao = utilisateurDao;
    }

    //Crée un nouvel utilisateur.
    public Utilisateur creerUtilisateur(String nom, String email) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'utilisateur ne peut pas être vide.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide.");
        }

        if (utilisateurDao.trouverParEmail(email) != null || utilisateurDao.trouverParEmail(email) != null) {
            throw new IllegalStateException("Un utilisateur avec cette adresse email existe déjà.");
        }

        Utilisateur nouvelUtilisateur = new Utilisateur(null, nom, email);
        return utilisateurDao.sauvegarder(nouvelUtilisateur);
    }

    public Utilisateur obtenirUtilisateur(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant ne peut pas être nul.");
        }
        return utilisateurDao.trouverParId(id);
    }

    public List<Utilisateur> obtenirTousLesUtilisateurs() {
        return utilisateurDao.trouverTous();
    }

    public void supprimerUtilisateur(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être nul.");
        }
        if (utilisateurDao.trouverParId(id) == null) {
            throw new IllegalArgumentException("Impossible de supprimer : utilisateur introuvable.");
        }
        utilisateurDao.supprimer(id);
    }
}