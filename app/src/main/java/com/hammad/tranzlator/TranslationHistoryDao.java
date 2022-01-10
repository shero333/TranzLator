package com.hammad.tranzlator;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TranslationHistoryDao {

    //insert data
    @Insert(onConflict = REPLACE)
    void addTranslatedData(TranslatedDataEntity translatedDataEntity);

    //delete
    @Delete
    void deleteTranslatedData(TranslatedDataEntity translatedDataEntity);

    //delete all data
    @Delete
    void deleteAllTranslatedData(List<TranslatedDataEntity> translatedDataEntityList);

    //update query
    @Query("UPDATE language_translation_history SET sourceLang = :sSrcLang, sourceLangCode = :sSrcCode, targetLang = :sTrgtlang, targetLangCode = :sTrgtCode,sourceText =:sSrcText,translatedText =:sTrgtText WHERE ID = :sID")
    void updateTranslatedData(int sID,String sSrcLang,String sSrcCode,String sTrgtlang,String sTrgtCode, String sSrcText, String sTrgtText);

    //get all data
    @Query("SELECT * FROM language_translation_history")
    List<TranslatedDataEntity> getAllTranslatedData();


}
