package com.example.sem08.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sem08.model.Lugar

@Database(entities = [Lugar::class],version = 1, exportSchema = false)
abstract class LugarDatabase() : RoomDatabase(){
    abstract fun LugarDao():LugarDao

    companion object{
        @Volatile
        private var INSTANCE: LugarDatabase? = null

        fun getDatabase(context: android.content.Context) : LugarDatabase {
            val temp = INSTANCE
            if (temp !=null){
                return temp
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LugarDatabase::class.java,
                    "lugar_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
