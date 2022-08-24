package com.risibleapps.translator.translate.translationLanguages;

public class TranslationLanguageModel {
    private String languageName;
    private String languageCode;

    public TranslationLanguageModel(String languageName, String languageCode) {
        this.languageName = languageName;
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

}
