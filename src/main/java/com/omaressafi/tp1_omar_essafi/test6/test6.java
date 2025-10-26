package com.omaressafi.tp1_omar_essafi.test6;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;

public class test6 {
    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        // Création du modèle avec logging
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-2.0-flash-exp")
                .logRequestsAndResponses(true)
                .build();

        // Création de l'assistant avec l'outil
        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatModel(model)
                .tools(new MeteoTool())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        // Tests
        System.out.println("=== Test 1: Météo à Paris ===");
        String reponse1 = assistant.chat("Quel temps fait-il à Paris ?");
        System.out.println(reponse1);

        System.out.println("\n=== Test 2: Ville inexistante ===");
        String reponse2 = assistant.chat("Quel temps fait-il à Zzzville ?");
        System.out.println(reponse2);

        System.out.println("\n=== Test 3: Question sans rapport avec météo ===");
        String reponse3 = assistant.chat("Qui a écrit Les Misérables ?");
        System.out.println(reponse3);
    }
}