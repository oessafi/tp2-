package com.omaressafi.tp1_omar_essafi;

/**
 * Record pour encapsuler une interaction avec le LLM.
 */
public record LlmInteraction(
        String questionJson,
        String reponseJson,
        String reponseExtraite
) {
}