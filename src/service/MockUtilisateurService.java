package service;

import modele.Role;
import modele.Utilisateur;

import java.util.ArrayList;
import java.util.List;

public class MockUtilisateurService {
    private List<Utilisateur> fauxUtilisateurs = new ArrayList<>();

    public Utilisateur creerUtilisateur(String nom, String email, Role role) {
        Utilisateur u = new Utilisateur((long) (fauxUtilisateurs.size() + 1), nom, email, role);
        fauxUtilisateurs.add(u);
        return u;
    }

    public List<Utilisateur> obtenirTousLesUtilisateurs() {
        return fauxUtilisateurs;
    }
}
