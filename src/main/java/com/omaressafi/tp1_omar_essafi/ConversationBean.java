package com.omaressafi.tp1_omar_essafi;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean pour la page de conversation.
 * Portée "view" pour conserver l'état de la conversation entre plusieurs requêtes POST.
 *
 * @author Omar Essafi
 */
@Named("bb")
@ViewScoped
public class ConversationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Liste des rôles système possibles
    private List<String> rolesSysteme;

    // Rôle système choisi par l'utilisateur
    private String roleSysteme;

    // Indique si le rôle système peut encore être changé
    private boolean roleSystemeChangeable = true;

    // Question de l'utilisateur
    private String question;

    // Réponse de l'API
    private String reponse;

    // Historique complet de la conversation
    private String conversation = "";

    /**
     * Constructeur : initialise la liste des rôles système disponibles
     */
    public ConversationBean() {
        rolesSysteme = new ArrayList<>();
        rolesSysteme.add("helpful assistant");
        rolesSysteme.add("traducteur français-anglais");
        rolesSysteme.add("guide touristique");
    }

    /**
     * Méthode action appelée quand l'utilisateur envoie une question.
     * @return null pour rester sur la même page (et dans la même vue)
     */
    public String envoyer() {
        // Vérifier que la question n'est pas vide
        if (question == null || question.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "La question ne peut pas être vide", null));
            return null;
        }

        // Le rôle ne peut plus être changé après le premier envoi
        roleSystemeChangeable = false;

        // Traitement de la question
        reponse = traiterQuestion(question);

        // Ajouter à l'historique de la conversation
        conversation += "Question : " + question + "\n\n";
        conversation += "Réponse : " + reponse + "\n\n";
        conversation += "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n";

        // Effacer la question pour la prochaine saisie
        question = "";

        // Retourner null pour rester sur la même page
        return null;
    }

    /**
     * TRAITEMENT BONUS PERSONNALISÉ : Analyseur de texte avec score de complexité
     *
     * Ce traitement calcule un "score de complexité" basé sur :
     * - Le nombre de mots
     * - La longueur moyenne des mots
     * - Le nombre de phrases (approximatif)
     * - Le pourcentage de lettres majuscules
     *
     * @param q la question de l'utilisateur
     * @return la réponse générée avec l'analyse complète
     */
    private String traiterQuestion(String q) {
        // Analyser le texte
        String[] mots = q.trim().split("\\s+");
        int nbMots = mots.length;
        int nbCaracteres = q.length();

        // Calculer la longueur moyenne des mots
        int totalLettres = 0;
        for (String mot : mots) {
            totalLettres += mot.length();
        }
        double longueurMoyenne = nbMots > 0 ? (double) totalLettres / nbMots : 0;

        // Compter approximativement le nombre de phrases
        int nbPhrases = q.split("[.!?]+").length;

        // Calculer le pourcentage de majuscules
        int nbMajuscules = 0;
        for (char c : q.toCharArray()) {
            if (Character.isUpperCase(c)) {
                nbMajuscules++;
            }
        }
        double pourcentageMajuscules = nbCaracteres > 0 ?
                (double) nbMajuscules / nbCaracteres * 100 : 0;

        // Compter les voyelles et consonnes
        int nbVoyelles = compterVoyelles(q);
        int nbConsonnes = compterConsonnes(q);

        // Calculer un score de complexité (formule personnalisée)
        int scoreComplexite = calculerScoreComplexite(nbMots, longueurMoyenne,
                nbPhrases, pourcentageMajuscules);

        // Déterminer le niveau de complexité
        String niveau = determinerNiveau(scoreComplexite);

        // Trouver le mot le plus long
        String motPlusLong = trouverMotPlusLong(mots);

        // Construire la réponse
        return construireReponse(nbMots, nbCaracteres, nbPhrases, longueurMoyenne,
                nbMajuscules, pourcentageMajuscules, nbVoyelles,
                nbConsonnes, scoreComplexite, niveau, motPlusLong);
    }

    /**
     * Compte le nombre de voyelles dans le texte
     */
    private int compterVoyelles(String texte) {
        int count = 0;
        String voyelles = "aeiouàâäéèêëïîôùûüÿæœAEIOUÀÂÄÉÈÊËÏÎÔÙÛÜŸÆŒ";
        for (char c : texte.toCharArray()) {
            if (voyelles.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }

    /**
     * Compte le nombre de consonnes dans le texte
     */
    private int compterConsonnes(String texte) {
        int count = 0;
        for (char c : texte.toCharArray()) {
            if (Character.isLetter(c) &&
                    "aeiouàâäéèêëïîôùûüÿæœAEIOUÀÂÄÉÈÊËÏÎÔÙÛÜŸÆŒ".indexOf(c) == -1) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calcule le score de complexité du texte
     */
    private int calculerScoreComplexite(int nbMots, double longueurMoyenne,
                                        int nbPhrases, double pourcentageMajuscules) {
        return (int) ((nbMots * 2) +
                (longueurMoyenne * 3) +
                (nbPhrases * 5) +
                (pourcentageMajuscules * 0.5));
    }

    /**
     * Détermine le niveau de complexité basé sur le score
     */
    private String determinerNiveau(int score) {
        if (score < 30) {
            return "Simple ⭐";
        } else if (score < 60) {
            return "Moyen ⭐⭐";
        } else if (score < 100) {
            return "Complexe ⭐⭐⭐";
        } else {
            return "Très complexe ⭐⭐⭐⭐";
        }
    }

    /**
     * Trouve le mot le plus long dans un tableau de mots
     */
    private String trouverMotPlusLong(String[] mots) {
        String motPlusLong = "";
        for (String mot : mots) {
            if (mot.length() > motPlusLong.length()) {
                motPlusLong = mot;
            }
        }
        return motPlusLong;
    }

    /**
     * Construit la réponse formatée avec toutes les statistiques
     */
    private String construireReponse(int nbMots, int nbCaracteres, int nbPhrases,
                                     double longueurMoyenne, int nbMajuscules,
                                     double pourcentageMajuscules, int nbVoyelles,
                                     int nbConsonnes, int scoreComplexite,
                                     String niveau, String motPlusLong) {
        StringBuilder sb = new StringBuilder();
        sb.append("|| Rôle : ").append(roleSysteme).append(" ||\n\n");
        sb.append("📊 ANALYSE DÉTAILLÉE DU TEXTE\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("📝 STATISTIQUES GÉNÉRALES\n");
        sb.append("   • Nombre de mots : ").append(nbMots).append("\n");
        sb.append("   • Nombre de caractères : ").append(nbCaracteres).append("\n");
        sb.append("   • Nombre de phrases : ").append(nbPhrases).append(" (environ)\n");
        sb.append("   • Longueur moyenne des mots : ").append(String.format("%.1f", longueurMoyenne)).append(" lettres\n");
        sb.append("   • Mot le plus long : \"").append(motPlusLong).append("\" (").append(motPlusLong.length()).append(" lettres)\n\n");

        sb.append("🔤 ANALYSE DES CARACTÈRES\n");
        sb.append("   • Voyelles : ").append(nbVoyelles).append("\n");
        sb.append("   • Consonnes : ").append(nbConsonnes).append("\n");
        sb.append("   • Majuscules : ").append(nbMajuscules).append(" (").append(String.format("%.1f", pourcentageMajuscules)).append("%)\n\n");

        sb.append("🎯 ÉVALUATION\n");
        sb.append("   • Score de complexité : ").append(scoreComplexite).append(" points\n");
        sb.append("   • Niveau : ").append(niveau).append("\n");

        return sb.toString();
    }

    /**
     * Méthode action pour démarrer un nouveau chat.
     * @return "index.xhtml" pour recharger la page et créer une nouvelle vue
     */
    public String nouveauChat() {
        // Retourner le nom de la page courante provoque la création d'une nouvelle vue
        // et donc d'une nouvelle instance du backing bean
        return "index.xhtml";
    }

    // ==================== GETTERS ET SETTERS ====================

    public List<String> getRolesSysteme() {
        return rolesSysteme;
    }

    public void setRolesSysteme(List<String> rolesSysteme) {
        this.rolesSysteme = rolesSysteme;
    }

    public String getRoleSysteme() {
        return roleSysteme;
    }

    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }

    public boolean isRoleSystemeChangeable() {
        return roleSystemeChangeable;
    }

    public void setRoleSystemeChangeable(boolean roleSystemeChangeable) {
        this.roleSystemeChangeable = roleSystemeChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }
}