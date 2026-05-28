package gestionincidents.dao;

import gestionincidents.model.Utilisateur;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDaoMemory implements UtilisateurDao {

    private final List<Utilisateur> utilisateursDb = new ArrayList<>();
    private long compteurId = 1L;

    @Override
    public Utilisateur sauvegarder(Utilisateur utilisateur) {
        if (utilisateur.getId() == null) {
            utilisateur.setId(compteurId++);
            utilisateursDb.add(utilisateur);
        } else {
            Utilisateur existant = trouverParId(utilisateur.getId());
            if (existant != null) {
                existant.setNom(utilisateur.getNom());
                existant.setEmail(utilisateur.getEmail());
            }
        }
        return utilisateur;
    }

    @Override
    public Utilisateur trouverParId(Long id) {
        return utilisateursDb.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Utilisateur trouverParEmail(String email) {
        return utilisateursDb.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Utilisateur> trouverTous() {
        return new ArrayList<>(utilisateursDb);
    }

    @Override
    public void supprimer(Long id) {
        utilisateursDb.removeIf(u -> u.getId().equals(id));
    }
}