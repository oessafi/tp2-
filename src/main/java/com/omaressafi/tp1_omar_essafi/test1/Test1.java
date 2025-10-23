package com.omaressafi.tp1_omar_essafi.test1;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class Test1 {
    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        // Création du modèle
        ChatLanguageModel modele = GoogleAiGeminiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-2.0-flash-exp")
                .temperature(0.7)
                .build();

        // Pose une question au modèle
        String reponse = modele.generate("Quelle est la capitale de la France ?");

        // Affiche la réponse du modèle
        System.out.println(reponse);
    }
}