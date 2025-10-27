package com.omaressafi.tp1_omar_essafi.test5;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.Scanner;

/**
 * Test 5 : RAG avancé avec PDF et conversation interactive
 */
public class test5 {

    interface Assistant {
        String chat(String question);
    }

    public static void main(String[] args) {
        // Récupération de la clé API
        String cle = System.getenv("GEMINI_KEY");



        System.out.println("Chargement du document PDF...");

        // 1. Chargement du document PDF
        // CORRECTION : Utilisation de FileSystemDocumentLoader qui gère les PDF automatiquement
        // grâce à langchain4j-easy-rag
        Document document = FileSystemDocumentLoader.loadDocument("langchain4j.pdf");

        System.out.println("Document chargé avec succès !");

        // 2. Modèle d'embeddings
        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(cle)
                .modelName("text-embedding-004")
                .build();

        // 3. Store d'embeddings en mémoire
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 4. Découpage et ingestion (Le RAG "manuel")
        System.out.println("Découpage du document et création des embeddings...");

        // Découpe le document en segments de 300 caractères avec 0 caractères de chevauchement
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);

        // Configure l'ingestor pour découper, créer les embeddings et stocker
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        // Lance l'ingestion
        ingestor.ingest(document);

        System.out.println("Embeddings créés et stockés !");

        // 5. Modèle de chat
        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)  // Température basse pour plus de précision avec RAG
                .build();

        // 6. Retriever - Recherche les segments les plus pertinents
        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)      // Retourne les 3 segments les plus pertinents
                .minScore(0.6)      // Score de similarité minimum
                .build();

        // 7. Assistant avec mémoire conversationnelle
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        // 8. Lancement de la conversation interactive
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Assistant RAG prêt ! (Basé sur langchain4j.pdf)");
        System.out.println("Tapez 'fin' pour quitter");
        System.out.println("=".repeat(60));

        conversationAvec(assistant);
    }

    /**
     * Méthode pour gérer la conversation interactive avec l'assistant
     */
    private static void conversationAvec(Assistant assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n" + "=".repeat(60));
                System.out.print("Posez votre question : ");
                String question = scanner.nextLine();

                // Ignorer les questions vides
                if (question.isBlank()) {
                    continue;
                }

                System.out.println("=".repeat(60));

                // Condition de sortie
                if ("fin".equalsIgnoreCase(question)) {
                    System.out.println("Au revoir !");
                    break;
                }

                // Envoi de la question à l'assistant
                System.out.println("Recherche dans le document...\n");
                String reponse = assistant.chat(question);

                System.out.println("Assistant : " + reponse);
                System.out.println("=".repeat(60));
            }
        }
    }
}