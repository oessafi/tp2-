package com.omaressafi.tp1_omar_essafi.test1;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class Test1 {
    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");



        // Création du modèle
        ChatModel modele = GoogleAiGeminiChatModel.builder()
                .apiKey(cle) // [cite: 224]
                .modelName("gemini-2.5-flash") // [cite: 225] Nom du modèle comme dans le cours
                .temperature(0.7) // Température demandée par le TP
                .build(); // [cite: 226]

        // Pose une question au modèle
        String reponse = modele.chat("Quelle est la capitale de la France ?");

        // Affiche la réponse du modèle
        System.out.println("Réponse du modèle :");
        System.out.println(reponse);
    }
}