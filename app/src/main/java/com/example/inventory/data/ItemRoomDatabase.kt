package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Item::class], version = 1, exportSchema = false)
//Set exportSchema to false, so as not to keep schema version history backups
abstract class ItemRoomDatabase: RoomDatabase(){

    //The database needs to know about the DAO. Inside the body of the class, declare an abstract function that returns the ItemDao. You can have multiple DAOs.
    abstract fun itemDao():ItemDao

    companion object{

        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase(context:Context):ItemRoomDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    ItemRoomDatabase::class.java,"item_database")
                    .fallbackToDestructiveMigration() // this will destroy and rebuild the database while migrating.
                    .build()
                INSTANCE = instance
                instance
            }
        }


    }

}