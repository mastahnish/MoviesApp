package pl.myosolutions.musicapp.db

import android.arch.lifecycle.LiveData
import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.Movie

interface IMoviesDataSource {

    val allMovies: LiveData<List<Movie>>

    fun getMovieById(id:Int): Flowable<Movie>

    fun insertAll(movies: List<Movie>)

    fun deleteAll()

}