package com.omaressafi.tp1_omar_essafi.test6;

import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MeteoTool {

    @Tool("Obtient les prévisions météo actuelles pour une ville donnée")
    public String getMeteo(String ville) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://wttr.in/" + ville + "?format=3"))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (IOException | InterruptedException e) {
            return "Impossible d'obtenir la météo pour " + ville;
        }
    }
}