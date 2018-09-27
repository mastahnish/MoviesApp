package pl.myosolutions.musicapp.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.Movie

@Dao
interface MovieDAO {

    @get:Query("SELECT * FROM movies")
    val allMovies: Flowable<List<Movie>>

    @Query("SELECT * FROM movies WHERE id=:movieId")
    fun getMovieById(movieId: Int): Flowable<Movie>

    @Insert
    fun insertMovies(movies: List<Movie>)

    @Query("DELETE FROM movies")
    fun deleteAllMovies()

}