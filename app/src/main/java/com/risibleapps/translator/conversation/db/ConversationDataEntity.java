package com.risibleapps.translator.conversation.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "conversation_history")
public class ConversationDataEntity implements Serializable{

    public ConversationDataEntity(){}

    public ConversationDataEntity(int conCode,String conSourceText,String conTranslatedText,String TTSCode)
    {
        this.conCode=conCode;
        this.conSourceText=conSourceText;
        this.conTranslatedText=conTranslatedText;
        this.TTSCode=TTSCode;
    }

    //column id
    @PrimaryKey(autoGenerate = true)
    private int conID;

    @ColumnInfo(name = "conversationCode")
    private int conCode;

    @ColumnInfo(name = "sourceText")
    private String conSourceText;

    @ColumnInfo(name = "translatedText")
    private String conTranslatedText;

    @ColumnInfo(name = "textToSpeechCode")
    private String TTSCode;

    public int getConID() {
        return conID;
    }

    public void setConID(int conID) {
        this.conID = conID;
    }

    public int getConCode() {
        return conCode;
    }

    public void setConCode(int conCode) {
        this.conCode = conCode;
    }

    public String getConSourceText() {
        return conSourceText;
    }

    public void setConSourceText(String conSourceText) {
        this.conSourceText = conSourceText;
    }

    public String getConTranslatedText() {
        return conTranslatedText;
    }

    public void setConTranslatedText(String conTranslatedText) {
        this.conTranslatedText = conTranslatedText;
    }

    public String getTTSCode() {
        return TTSCode;
    }

    public void setTTSCode(String TTSCode) {
        this.TTSCode = TTSCode;
    }

}
