package com.example.spin36.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spin36.data.database.dao.CasinoDAO
import com.example.spin36.data.database.entities.JugadorEntity
import com.example.spin36.data.database.entities.PartidaEntity
import android.content.Context
import androidx.room.Room
import com.example.spin36.data.database.entities.SesionEntity

@Database(entities = [JugadorEntity::class, PartidaEntity::class, SesionEntity::class],
    version=2, exportSchema = false )
abstract class Spin36Database: RoomDatabase() {
    abstract fun CasinoDAO(): CasinoDAO

    companion object {
        @Volatile
        private var INSTANCE: Spin36Database? = null

        fun getDatabase(context: Context): Spin36Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Spin36Database::class.java,
                    "spin36_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}