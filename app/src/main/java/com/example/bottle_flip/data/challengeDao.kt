package com.example.bottle_flip.data
import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.bottle_flip.model.Challenge

@Dao
interface ChallengeDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChallenge(challenge: Challenge)

    @Query("SELECT * FROM challenge")
    suspend fun getListchallenge(): MutableList<Challenge>

    @Delete
    suspend fun deletechallenge(challenge: Challenge)

    @Update
    suspend fun updatechallenge(challenge: Challenge)
}