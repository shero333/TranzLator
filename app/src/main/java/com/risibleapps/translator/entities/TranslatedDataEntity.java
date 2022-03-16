package com.risibleapps.translator.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//creating table name
@Entity(tableName = "language_translation_history")

public class TranslatedDataEntity implements Serializable {

    //column names for tables

    //column id
    @PrimaryKey(autoGenerate = true)
    private int ID;

    @ColumnInfo(name = "sourceLang")
    private String sourceLang;

    @ColumnInfo(name = "sourceLangCode")
    private String sourceLangCode;

    @ColumnInfo(name = "sourceText")
    private String sourceText;

    @ColumnInfo(name = "targetLang")
    private String targetLang;

    @ColumnInfo(name = "targetLangCode")
    private String targetLangCode;

    @ColumnInfo(name = "translatedText")
    private String translatedText;

    //getter and setter

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getSourceLangCode() {
        return sourceLangCode;
    }

    public void setSourceLangCode(String sourceLangCode) {
        this.sourceLangCode = sourceLangCode;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public String getTargetLangCode() {
        return targetLangCode;
    }

    public void setTargetLangCode(String targetLangCode) {
        this.targetLangCode = targetLangCode;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}
