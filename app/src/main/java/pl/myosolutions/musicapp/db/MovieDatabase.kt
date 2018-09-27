package pl.myosolutions.musicapp.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import pl.myosolutions.musicapp.db.MovieDatabase.Companion.DATABASE_VERSION
import pl.myosolutions.musicapp.model.Movie

@Database(entities = arrayOf(Movie::class), version = DATABASE_VERSION, exportSchema = false)
abstract class MovieDatabase: RoomDatabase() {

    abstract fun movieDAO(): MovieDAO

    companion object {
        const val DATABASE_VERSION = 1
        val DATABASE_NAME = "Movies-Database.db"

        private var mInstance: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase{
            if(mInstance == null)
                mInstance = Room.databaseBuilder(context, MovieDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()

            return mInstance!!
        }

    }
}