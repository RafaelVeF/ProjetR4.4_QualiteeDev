#  Plateforme de Gestion d'Incidents Informatiques - Frontend (Sujet 2)

Ce projet s'inscrit dans le cadre du module "Qualité logicielle, intégration et validation des systèmes" du BUT2 Informatique (Université de Picardie Jules Verne).
Il correspond au développement du **Sujet 2 : Validation fonctionnelle et parcours utilisateur**. 
Ce dépôt contient le code de la couche de présentation (Interface Utilisateur) et ses tests de validation, développé en faisant abstraction du fonctionnement réel du backend (base de données).

##  Équipe
* **DEVISSCHER Baptiste**
* **VENEQUE--FOUGERAY Rafael**
* **Groupe** : G2.2, RACA

---

## Prérequis et Installation

L'application est un projet Java natif, sans gestionnaire de dépendances lourd, conçu pour être facilement déployable.
* **Environnement requis** : Java JDK 8 ou supérieur.
* **IDE Recommandé** : IntelliJ IDEA (le fichier de configuration `.iml` est inclus).

### Comment lancer l'application
1. Clonez ce dépôt ou ouvrez le dossier dans IntelliJ IDEA.
2. Naviguez dans l'arborescence jusqu'au fichier d'entrée : `src/Main.java`.
3. Lancez la méthode `main()`.
4. L'interface d'accueil s'ouvrira, préchargée avec des données de démonstration simulées en mémoire.

---

## Architecture du Frontend (Java Swing)

Notre frontend repose intégralement sur la bibliothèque **Java Swing**. L'interface est découpée en plusieurs fenêtres (Views) spécialisées, agnostiques du système de stockage des données :

* **`InterfaceAcceuil.java` (Tableau de bord)** : Contient un `JTable` dynamique. Le tri des tickets est géré en temps réel via un `TableRowSorter` connecté à la barre de recherche, sans nécessiter de recharger la page.
* **`InterfaceCreation.java` (Formulaire d'ajout)** : Fenêtre modale qui capture les saisies, les nettoie (ex: `.trim()`) et intercepte les potentielles erreurs pour les afficher visuellement en rouge, protégeant ainsi l'application des crashs.
* **`InterfaceModification.java` (Formulaire de gestion)** : Gère l'évolution du cycle de vie du ticket en pré-remplissant automatiquement les champs selon les données existantes.

### Sécurité Visuelle et Habilitations (RBAC)
L'interface s'adapte dynamiquement au profil de l'utilisateur connecté :
* **Un utilisateur `CLASSIQUE`** verra son tableau de bord purgé des tickets appartenant aux autres, et les boutons d'administration seront purement et simplement retirés de l'écran.
* **Un utilisateur `TECHNICIEN`** verra tous les tickets, et les boutons d'administration s'activeront intelligemment au clic sur une ligne du tableau.

---

## Tests et Validation de l'Interface

Tester une interface graphique de manière automatisée est un défi. Pour valider notre UI sans action manuelle, nous avons développé une suite de **19 tests fonctionnels** avec **JUnit 5**.

### Exécuter les tests sous IntelliJ
1. Vérifiez que le dossier `test/` est marqué comme dossier de test (en vert). Sinon : *Clic droit sur le dossier > Mark Directory as > Test Sources Root*.
2. Faites un clic droit sur le dossier `test/` > **Run 'Tests in 'test''** (ou `Ctrl + Shift + F10`).
3. Tous les tests s'exécuteront à la volée.

### Stratégie de Test (Réflexion Java)
Puisque nos composants graphiques (`JButton`, `JTextField`) sont strictement encapsulés (privés) pour respecter les bonnes pratiques, nos tests utilisent l'API `java.lang.reflect` (Introspection). 
Le script de test scanne la fenêtre en arrière-plan, repère les boutons, injecte des textes de tests extrêmes (ex: `<script>` ou 5000 caractères) et simule des clics virtuels. Cela nous garantit une couverture complète (limites, rôles, erreurs) à la vitesse de la machine.
