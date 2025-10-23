package com.omaressafi.tp1_omar_essafi;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LlmClient {
    private String systemRole;
    private Assistant assistant;
    private ChatMemory chatMemory;

    public LlmClient() {
        String cle = System.getenv("GEMINI_KEY");

        // Création du modèle
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-2.0-flash-exp")
                .temperature(0.7)
                .build();

        // Création de la mémoire
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Création de l'assistant
        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    public void setSystemRole(String role) {
        this.systemRole = role;
        chatMemory.clear();
        chatMemory.add(SystemMessage.from(role));
    }

    public String envoyerQuestion(String question) {
        return assistant.chat(question);
    }
}