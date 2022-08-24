package com.risibleapps.translator.conversation.db;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ConversationDao {

    //insert data
    @Insert(onConflict = REPLACE)
    void addConversationData(ConversationDataEntity conversationDataEntity);

    //delete all data
    @Delete
    void deleteAllConversationData(List<ConversationDataEntity> conversationDataEntityList);

    //get all data in descending order(largest to smallest)
    @Query("SELECT * FROM conversation_history ORDER BY conID ASC")
    List<ConversationDataEntity> getAllConversationData();
}
