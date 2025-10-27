package com.omaressafi.tp1_omar_essafi.test6;

import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Outil météo qui utilise l'API wttr.in
 */
public class MeteoTool {

    /**
     * Obtient la météo actuelle d'une ville
     * @param ville Le nom de la ville
     * @return Les informations météo de la ville
     */
    @Tool("Obtient la météo actuelle d'une ville donnée en temps réel")
    public String obtenirMeteo(String ville) {
        try {
            // Création du client HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Création de la requête vers wttr.in
            // format=3 donne un format court: "Ville: 🌤️ +15°C"
            String url = "https://wttr.in/" + ville + "?format=3";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // Envoi de la requête et récupération de la réponse
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            // Retourne le corps de la réponse
            return response.body();

        } catch (IOException | InterruptedException e) {
            return "Impossible d'obtenir la météo pour " + ville +
                    ". Erreur: " + e.getMessage();
        }
    }

    // Méthode de test pour tester l'outil directement
    public static void main(String[] args) {
        MeteoTool tool = new MeteoTool();

        System.out.println("Test de l'outil météo:");
        System.out.println("Paris: " + tool.obtenirMeteo("Paris"));
        System.out.println("Casablanca: " + tool.obtenirMeteo("Casablanca"));
        System.out.println("Tokyo: " + tool.obtenirMeteo("Tokyo"));
    }
}