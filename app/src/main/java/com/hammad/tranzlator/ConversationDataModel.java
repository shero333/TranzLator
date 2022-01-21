package com.hammad.tranzlator;

public class ConversationDataModel {
    int buttonPressedID;
    String speechToText;
    String translatedText;
    String code;

    public ConversationDataModel()
    {}
    public ConversationDataModel(int buttonPressedID, String speechToText, String translatedText, String code) {
        this.buttonPressedID = buttonPressedID;
        this.speechToText = speechToText;
        this.translatedText = translatedText;
        this.code = code;
    }

    public int getButtonPressedID() {
        return buttonPressedID;
    }

    public void setButtonPressedID(int buttonPressedID) {
        this.buttonPressedID = buttonPressedID;
    }

    public String getSpeechToText() {
        return speechToText;
    }

    public void setSpeechToText(String speechToText) {
        this.speechToText = speechToText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
