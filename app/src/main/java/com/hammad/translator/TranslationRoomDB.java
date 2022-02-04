package com.hammad.translator;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hammad.translator.entities.ConversationDataEntity;
import com.hammad.translator.entities.TranslatedDataEntity;

//Add database entities
@Database(entities = {TranslatedDataEntity.class, ConversationDataEntity.class},version = 1,exportSchema = false)

public abstract class TranslationRoomDB extends RoomDatabase {

    //create database instance
    public static TranslationRoomDB instance;

    //define database name
    public static String DATABASE_NAME="translation_db";

    public synchronized static TranslationRoomDB getInstance(Context context)
    {
        //check instance
        if(instance==null)
        {
            //when DB is null. Initialize it
            instance= Room.databaseBuilder(context,
                    TranslationRoomDB.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    //create DAO
    public abstract TranslationHistoryDao translationHistoryDao();

    //create DAO
    public abstract ConversationDao conversationDao();
}
