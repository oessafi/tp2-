package com.omaressafi.tp1_omar_essafi.test4;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * Le RAG facile !
 */
public class Test4 {

    // Assistant conversationnel
    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String llmKey = System.getenv("GEMINI_KEY");

        // Modèle de chat
        ChatLanguageModel modele = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-2.0-flash-exp")
                .temperature(0.3)
                .build();

        // Chargement du document
        String nomDocument = "infos.txt";
        Document document = FileSystemDocumentLoader.loadDocument(nomDocument);

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Calcule les embeddings et les enregistre dans la base vectorielle
        EmbeddingStoreIngestor.ingest(document, embeddingStore);

        // Création de l'assistant conversationnel avec mémoire et RAG
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(modele)  // ← CHANGÉ ICI aussi
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        // Question
        String question = "Comment s'appelle le chat de Pierre ?";
        String reponse = assistant.chat(question);

        System.out.println(reponse);
    }
}