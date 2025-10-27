package com.omaressafi.tp1_omar_essafi.test6;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * Test 6 : Utilisation d'un outil avec LangChain4j
 * L'outil permet au LLM d'obtenir la météo en temps réel
 */
public class test6 {

    public static void main(String[] args) {
        // Récupération de la clé API
        String cle = System.getenv("GEMINI_KEY");
        if (cle == null) {
            System.err.println("Erreur : La variable d'environnement GEMINI_API_KEY n'est pas définie.");
            return;
        }

        System.out.println("Initialisation de l'assistant météo...\n");

        // ✅ Utilisation de ChatModel comme demandé
        // ✅ Ajout de logRequestsAndResponses(true) pour voir les JSON
        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-2.5-flash")
                .temperature(0.7)
                .logRequestsAndResponses(true)  // ⭐ Active le logging des JSON
                .build();

        System.out.println("Assistant météo prêt !\n");
        System.out.println("=".repeat(70));

        // ========== Test 1: Météo d'une ville réelle ==========
        System.out.println("\n=== Test 1: Météo à Paris ===");
        // ✅ Créer un assistant sans mémoire pour éviter les conflits
        AssistantMeteo assistant1 = AiServices.builder(AssistantMeteo.class)
                .chatModel(chatModel)
                .tools(new MeteoTool())
                // PAS de chatMemory pour éviter l'erreur INVALID_ARGUMENT
                .build();

        String reponse1 = assistant1.chat("Quel temps fait-il à Paris ?");
        System.out.println("Réponse: " + reponse1);
        System.out.println("=".repeat(70));

        // ========== Test 2: Ville inexistante ==========
        System.out.println("\n=== Test 2: Ville inexistante ===");
        AssistantMeteo assistant2 = AiServices.builder(AssistantMeteo.class)
                .chatModel(chatModel)
                .tools(new MeteoTool())
                .build();

        String reponse2 = assistant2.chat("Quel temps fait-il à Zzzville ?");
        System.out.println("Réponse: " + reponse2);
        System.out.println("=".repeat(70));

        // ========== Test 3: Question sans rapport avec la météo ==========
        System.out.println("\n=== Test 3: Question sans rapport avec météo ===");
        AssistantMeteo assistant3 = AiServices.builder(AssistantMeteo.class)
                .chatModel(chatModel)
                .tools(new MeteoTool())
                .build();

        String reponse3 = assistant3.chat("Qui a écrit Les Misérables ?");
        System.out.println("Réponse: " + reponse3);
        System.out.println("=".repeat(70));

        // ========== Test 4: Question contextuelle sur la météo ==========
        System.out.println("\n=== Test 4: Conseil parapluie ===");
        AssistantMeteo assistant4 = AiServices.builder(AssistantMeteo.class)
                .chatModel(chatModel)
                .tools(new MeteoTool())
                .build();

        String reponse4 = assistant4.chat("J'ai prévu d'aller aujourd'hui à Londres. Est-ce que tu me conseilles de prendre un parapluie ?");
        System.out.println("Réponse: " + reponse4);
        System.out.println("=".repeat(70));

        // ========== Test 5: Comparaison de plusieurs villes ==========
        System.out.println("\n=== Test 5: Comparaison météo ===");
        AssistantMeteo assistant5 = AiServices.builder(AssistantMeteo.class)
                .chatModel(chatModel)
                .tools(new MeteoTool())
                .build();

        String reponse5 = assistant5.chat("Compare la météo entre Casablanca et Paris");
        System.out.println("Réponse: " + reponse5);
        System.out.println("=".repeat(70));

        System.out.println("\n✅ Tous les tests terminés !");
        System.out.println("\n📊 Analyse des logs JSON ci-dessus:");
        System.out.println("1. Cherchez 'tools.functionDeclarations' → Déclaration de l'outil au LLM");
        System.out.println("2. Cherchez 'functionCall' → Le LLM décide d'utiliser l'outil");
        System.out.println("3. Cherchez 'functionResponse' → Résultat retourné par l'outil");
        System.out.println("4. Cherchez le texte final → Réponse reformulée par le LLM");
    }
}