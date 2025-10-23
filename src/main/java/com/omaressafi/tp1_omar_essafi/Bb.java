package com.omaressafi.tp1_omar_essafi;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("bb")
@ViewScoped
public class Bb implements Serializable {

    @Inject
    private LlmClient llmClient;

    private String question;
    private String reponse;
    private String conversation;
    private String roleSysteme;
    private List<String> rolesSysteme;
    private boolean roleSystemeChangeable = true;

    @PostConstruct
    public void init() {
        rolesSysteme = new ArrayList<>();
        rolesSysteme.add("Tu es un assistant serviable");
        rolesSysteme.add("Tu es un expert en informatique");
        rolesSysteme.add("Tu es un professeur de français");
        rolesSysteme.add("Tu réponds toujours en rimes");

        conversation = "";
    }

    public void envoyer() {
        if (question == null || question.trim().isEmpty()) {
            return;
        }

        // Premier message : définir le rôle système
        if (conversation.isEmpty() && roleSysteme != null) {
            llmClient.setSystemRole(roleSysteme);
            roleSystemeChangeable = false;
        }

        // Envoyer la question
        reponse = llmClient.envoyerQuestion(question);

        // Mettre à jour la conversation
        conversation += "Q: " + question + "\n\n";
        conversation += "R: " + reponse + "\n\n";
        conversation += "---\n\n";

        // Réinitialiser la question
        question = "";
    }

    public void nouveauChat() {
        question = "";
        reponse = "";
        conversation = "";
        roleSystemeChangeable = true;
    }

    // Getters et Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }

    public String getConversation() { return conversation; }
    public void setConversation(String conversation) { this.conversation = conversation; }

    public String getRoleSysteme() { return roleSysteme; }
    public void setRoleSysteme(String roleSysteme) { this.roleSysteme = roleSysteme; }

    public List<String> getRolesSysteme() { return rolesSysteme; }

    public boolean isRoleSystemeChangeable() { return roleSystemeChangeable; }
}