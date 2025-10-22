package com.omaressafi.tp1_omar_essafi;

/**
 * Exception levée si on envoie une mauvaise requête à l'API du LLM.
 */
public class RequeteException extends Exception {

    private int status;
    private String requeteJson;

    public RequeteException() {
        super();
    }

    public RequeteException(String message) {
        super(message);
    }

    public RequeteException(String message, String requeteJson) {
        super(message);
        this.requeteJson = requeteJson;
    }

    public RequeteException(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getRequeteJson() {
        return requeteJson;
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (requeteJson != null) {
            msg += "\nRequête JSON:\n" + requeteJson;
        }
        return msg;
    }
}