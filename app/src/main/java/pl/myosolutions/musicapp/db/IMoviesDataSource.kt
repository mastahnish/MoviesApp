package pl.myosolutions.musicapp.db

import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.Movie

interface IMoviesDataSource {

    val allMovies: Flowable<List<Movie>>

    fun getMovieById(id:Int):Flowable<Movie>

    fun insertAll(movies:List<Movie>)

    fun deleteAll()

}