package com.omaressafi.tp1_omar_essafi;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Filtre pour forcer l'encodage UTF-8 des requêtes et réponses
 */
@WebFilter(filterName = "EncodingFilter", urlPatterns = {"/*"})
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Forcer l'encodage UTF-8 pour les requêtes
        request.setCharacterEncoding("UTF-8");

        // Forcer l'encodage UTF-8 pour les réponses (optionnel, décommenté si nécessaire)
        // response.setCharacterEncoding("UTF-8");
        // response.setContentType("text/html; charset=UTF-8");

        // Continuer la chaîne de filtres
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }
}