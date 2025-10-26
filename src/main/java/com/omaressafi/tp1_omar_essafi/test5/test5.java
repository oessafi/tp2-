package com.omaressafi.tp1_omar_essafi.test5;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
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
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class test5 {
    interface Assistant {
        String chat(String question);
    }

    // Parser PDF personnalisé pour PDFBox 3.x
    static class PdfBoxParser implements DocumentParser {
        @Override
        public Document parse(InputStream inputStream) {
            try (PDDocument pdfDocument = Loader.loadPDF(inputStream.readAllBytes())) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(pdfDocument);
                return Document.from(text);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors du parsing du PDF", e);
            }
        }
    }

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        // Chargement du PDF
        Path documentPath = Paths.get("langchain4j.pdf");
        Document document;

        try (InputStream inputStream = Files.newInputStream(documentPath)) {
            PdfBoxParser parser = new PdfBoxParser();
            document = parser.parse(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier PDF", e);
        }

        // Modèle d'embeddings
        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(cle)
                .modelName("text-embedding-004")
                .build();

        // Store d'embeddings
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Découpage et ingestion
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(document);

        // Modèle de chat
        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-2.0-flash-exp")
                .build();

        // Retriever
        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.6)
                .build();

        // Assistant avec mémoire
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        // Conversation interactive
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nQuestion (ou 'quit' pour quitter) : ");
            String question = scanner.nextLine();

            if (question.equalsIgnoreCase("quit")) break;

            String reponse = assistant.chat(question);
            System.out.println("\nRéponse : " + reponse);
        }

        scanner.close();
    }
}