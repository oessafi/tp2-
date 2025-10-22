package com.omaressafi.tp1_omar_essafi;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour gérer le JSON des requêtes à l'API de Gemini.
 * Contient l'état JSON de la conversation et des méthodes pour manipuler le JSON.
 */
@Dependent
public class JsonUtilPourGemini implements Serializable {

    private static final long serialVersionUID = 1L;

    private String systemRole;

    /**
     * Pour ajouter une nouvelle valeur à la fin du tableau JSON "contents".
     * Le "-" final indique que la valeur sera ajoutée à la fin du tableau.
     */
    private final JsonPointer pointer = Json.createPointer("/contents/-");

    /**
     * Requête JSON, construite progressivement.
     */
    private JsonObject requeteJson;
    private String texteRequeteJson;

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }

    /**
     * Client pour envoyer des requêtes à l'API de Gemini.
     */
    @Inject
    private LlmClientPourGemini geminiClient;

    /**
     * Envoie une requête à l'API de Gemini.
     *
     * @param question question posée par l'utilisateur
     * @return un LlmInteraction contenant la requête, la réponse JSON et la réponse extraite
     * @throws RequeteException si la requête échoue
     */
    public LlmInteraction envoyerRequete(String question) throws RequeteException {
        String requestBody;

        if (this.requeteJson == null) {
            // Première question : créer la requête avec le rôle système
            requestBody = creerRequeteJson(this.systemRole, question);
        } else {
            // Questions suivantes : ajouter la question à la conversation
            requestBody = ajouteQuestionDansJsonRequete(question);
        }

        Entity<String> entity = Entity.entity(requestBody, MediaType.APPLICATION_JSON_TYPE);

        // Pour afficher la requête JSON dans la page JSF
        this.texteRequeteJson = prettyPrinting(requeteJson);

        System.out.println("=== REQUÊTE ENVOYÉE À GEMINI ===");
        System.out.println(requestBody);
        System.out.println("================================");

        // Envoi de la requête
        try (Response response = geminiClient.envoyerRequete(entity)) {
            int status = response.getStatus();
            String texteReponseJson = response.readEntity(String.class);

            System.out.println("=== RÉPONSE REÇUE DE GEMINI ===");
            System.out.println("Status : " + status);
            System.out.println("Début de la réponse : " +
                    texteReponseJson.substring(0, Math.min(300, texteReponseJson.length())) + "...");
            System.out.println("===============================");

            if (status == 200) {
                return new LlmInteraction(
                        this.texteRequeteJson,
                        texteReponseJson,
                        extractReponse(texteReponseJson)
                );
            } else {
                // En cas d'erreur, afficher tous les détails
                System.err.println("=== ERREUR API GEMINI ===");
                System.err.println("Status : " + status);
                System.err.println("Réponse complète : " + texteReponseJson);
                System.err.println("========================");

                JsonObject objet = Json.createReader(new StringReader(requestBody)).readObject();
                throw new RequeteException(
                        status + " : " + response.getStatusInfo() + "\n\nDétails de l'erreur:\n" + texteReponseJson,
                        prettyPrinting(objet)
                );
            }
        } catch (RequeteException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("=== EXCEPTION LORS DE L'ENVOI ===");
            System.err.println("Message : " + e.getMessage());
            e.printStackTrace();
            System.err.println("=================================");

            JsonObject objet = Json.createReader(new StringReader(requestBody)).readObject();
            throw new RequeteException(
                    "Exception lors de l'envoi : " + e.getMessage(),
                    prettyPrinting(objet)
            );
        }
    }

    /**
     * Crée la première requête JSON SANS system_instruction.
     * Le rôle système est intégré dans la première question.
     */
    private String creerRequeteJson(String systemRole, String question) {
        // Combine le rôle système avec la question
        String questionComplete = systemRole + "\n\nQuestion: " + question;

        // User content
        JsonArray userContentParts = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("text", questionComplete))
                .build();
        JsonObject userContent = Json.createObjectBuilder()
                .add("role", "user")
                .add("parts", userContentParts)
                .build();
        JsonArray contents = Json.createArrayBuilder()
                .add(userContent)
                .build();

        // Root object SANS system_instruction
        JsonObject rootJson = Json.createObjectBuilder()
                .add("contents", contents)
                .build();

        this.requeteJson = rootJson;
        return rootJson.toString();
    }

    /**
     * Ajoute une nouvelle question à la conversation.
     */
    private String ajouteQuestionDansJsonRequete(String nouvelleQuestion) {
        JsonObject nouveauMessageJson = Json.createObjectBuilder()
                .add("text", nouvelleQuestion)
                .build();

        JsonObjectBuilder newPartBuilder = Json.createObjectBuilder()
                .add("role", "user")
                .add("parts", Json.createArrayBuilder().add(nouveauMessageJson).build());

        this.requeteJson = this.pointer.add(this.requeteJson, newPartBuilder.build());
        this.texteRequeteJson = prettyPrinting(requeteJson);
        return this.requeteJson.toString();
    }

    /**
     * Formate le JSON pour un affichage lisible.
     */
    private String prettyPrinting(JsonObject jsonObject) {
        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(config);
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
            jsonWriter.write(jsonObject);
        }
        return stringWriter.toString();
    }

    /**
     * Extrait la réponse du JSON et l'ajoute à la conversation.
     */
    private String extractReponse(String json) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(json))) {
            JsonObject jsonObject = jsonReader.readObject();
            JsonObject messageReponse = jsonObject
                    .getJsonArray("candidates")
                    .getJsonObject(0)
                    .getJsonObject("content");

            // Ajoute la réponse à la conversation
            this.requeteJson = this.pointer.add(this.requeteJson, messageReponse);

            // Extrait le texte de la réponse
            return messageReponse.getJsonArray("parts").getJsonObject(0).getString("text");
        }
    }
}