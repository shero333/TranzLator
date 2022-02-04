package com.hammad.translator;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.hammad.translator.entities.TranslatedDataEntity;

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

    //get all data in descending order(largest to smallest)
    @Query("SELECT * FROM language_translation_history ORDER BY ID DESC")
    List<TranslatedDataEntity> getAllTranslatedData();

}
