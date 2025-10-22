package com.omaressafi.tp1_omar_essafi;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean pour la page de conversation avec Gemini.
 *
 * @author Omar Essafi
 */
@Named("bb")
@ViewScoped
public class Bb implements Serializable {

    private static final long serialVersionUID = 1L;

    // Liste des rôles système possibles
    private List<String> rolesSysteme;

    // Rôle système choisi
    private String roleSysteme;

    // Indique si le rôle peut encore être changé
    private boolean roleSystemeChangeable = true;

    // Question de l'utilisateur
    private String question;

    // Réponse du LLM
    private String reponse;

    // Historique de la conversation
    private String conversation = "";

    // Mode debug activé/désactivé
    private boolean debug = false;

    // JSON de la requête (mode debug)
    private String texteRequeteJson = "";

    // JSON de la réponse (mode debug)
    private String texteReponseJson = "";

    @Inject
    private JsonUtilPourGemini jsonUtil;

    @Inject
    private FacesContext facesContext;

    /**
     * Constructeur : initialise les rôles système
     */
    public Bb() {
        rolesSysteme = new ArrayList<>();
        rolesSysteme.add("helpful assistant");
        rolesSysteme.add("traducteur français-anglais");
        rolesSysteme.add("guide touristique");
        // BONUS : Ajoutez votre rôle personnalisé ici !
        // rolesSysteme.add("votre rôle personnalisé");
    }

    /**
     * Envoie la question à l'API Gemini
     */
    public String envoyer() {
        // Validation
        if (question == null || question.trim().isEmpty()) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "La question ne peut pas être vide", null));
            return null;
        }

        // Le rôle ne peut plus être changé après le premier envoi
        if (roleSystemeChangeable) {
            roleSystemeChangeable = false;
            // Définir le rôle système selon le choix
            String roleSystemeComplet = getRoleSystemeComplet();
            jsonUtil.setSystemRole(roleSystemeComplet);
        }

        try {
            // Envoyer la requête à Gemini
            LlmInteraction interaction = jsonUtil.envoyerRequete(question);

            // Récupérer les résultats
            this.reponse = interaction.reponseExtraite();
            this.texteRequeteJson = interaction.questionJson();
            this.texteReponseJson = interaction.reponseJson();

            // Ajouter à l'historique
            conversation += "Question : " + question + "\n\n";
            conversation += "Réponse : " + reponse + "\n\n";
            conversation += "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n";

            // Effacer la question
            question = "";

        } catch (Exception e) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Problème de connexion avec l'API Gemini",
                    "Erreur : " + e.getMessage()
            );
            facesContext.addMessage(null, message);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retourne le rôle système complet selon le choix
     */
    private String getRoleSystemeComplet() {
        switch (roleSysteme) {
            case "helpful assistant":
                return "Tu es un assistant serviable qui répond de manière claire et concise.";
            case "traducteur français-anglais":
                return "Tu es un traducteur professionnel français-anglais. " +
                        "Traduis uniquement le texte fourni, sans ajouter d'explications.";
            case "guide touristique":
                return "Tu es un guide touristique enthousiaste et connaisseur. " +
                        "Fournis des informations intéressantes sur les lieux touristiques.";
            // BONUS : Ajoutez votre rôle personnalisé ici !
            default:
                return "Tu es un assistant serviable.";
        }
    }

    /**
     * Bascule le mode debug
     */
    public void toggleDebug() {
        this.debug = !this.debug;
    }

    /**
     * Retourne l'encodage de la requête courante
     */
    public String getEncodageActuel() {
        if (facesContext != null && facesContext.getExternalContext() != null) {
            String encoding = facesContext.getExternalContext().getRequestCharacterEncoding();
            return encoding != null ? encoding : "Non défini";
        }
        return "Non disponible";
    }

    /**
     * Démarre un nouveau chat
     */
    public String nouveauChat() {
        return "index.xhtml";
    }

    // ==================== GETTERS ET SETTERS ====================

    public List<String> getRolesSysteme() {
        return rolesSysteme;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public String getConversation() {
        return conversation;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getTexteRequeteJson() {
        return texteRequeteJson;
    }

    public void setTexteRequeteJson(String texteRequeteJson) {
        this.texteRequeteJson = texteRequeteJson;
    }

    public String getTexteReponseJson() {
        return texteReponseJson;
    }

    public void setTexteReponseJson(String texteReponseJson) {
        this.texteReponseJson = texteReponseJson;
    }
}