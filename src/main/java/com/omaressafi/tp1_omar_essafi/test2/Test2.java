package com.omaressafi.tp1_omar_essafi.test2;

import dev.langchain4j.model.chat.ChatLanguageModel;  // ← CHANGÉ
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

public class Test2 {
    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatLanguageModel modele = GoogleAiGeminiChatModel.builder()  // ← CHANGÉ
                .apiKey(cle)
                .modelName("gemini-2.0-flash-exp")
                .temperature(0.7)
                .build();

        // Création du template
        PromptTemplate template = PromptTemplate.from(
                "Traduis le texte suivant en anglais : {{texte}}"
        );

        // Application du template
        Prompt prompt = template.apply(Map.of("texte", "Bonjour, comment allez-vous ?"));

        // Envoi au modèle
        String reponse = modele.generate(prompt.text());  // ← CHANGÉ
        System.out.println(reponse);
    }
}