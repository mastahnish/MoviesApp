package pl.myosolutions.musicapp.db

import android.arch.lifecycle.LiveData
import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.Movie
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MoviesDataSource(private val movieDAO: MovieDAO): IMoviesDataSource {

    companion object {
        private var mInstance: MoviesDataSource? = null

        fun getInstance(movieDAO: MovieDAO): MoviesDataSource{
            if(mInstance == null)
                mInstance = MoviesDataSource(movieDAO)
            return mInstance!!
        }
    }

    private val executor: Executor = Executors.newSingleThreadExecutor()

    override val allMovies: LiveData<List<Movie>>
        get() = movieDAO.allMovies

    override fun getMovieById(id: Int): Flowable<Movie> {
        return movieDAO.getMovieById(id)
    }

    override fun insertAll(movies: List<Movie>) {
        executor.execute {  movieDAO.insertMovies(movies) }
    }

    override fun deleteAll() {
        movieDAO.deleteAllMovies()
    }






}