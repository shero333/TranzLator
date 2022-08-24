package com.risibleapps.translator.conversation.conversationLanguages;

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

    public String getConLanguageCode() {
        return conLanguageCode;
    }

}
