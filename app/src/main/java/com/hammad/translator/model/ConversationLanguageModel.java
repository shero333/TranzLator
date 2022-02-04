package com.hammad.translator.model;

public class ConversationLanguageModel {

    String conLanguageName;
    String conLanguageCode;

    public ConversationLanguageModel(String cLanguageName, String cLanguageCode) {
        this.conLanguageName = cLanguageName;
        this.conLanguageCode = cLanguageCode;
    }

    public String getConLanguageName() {
        return conLanguageName;
    }

    public void setConLanguageName(String cLanguageName) {
        this.conLanguageName = cLanguageName;
    }

    public String getConLanguageCode() {
        return conLanguageCode;
    }

    public void setConLanguageCode(String cLanguageCode) {
        this.conLanguageCode = cLanguageCode;
    }
}
