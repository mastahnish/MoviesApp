package pl.myosolutions.musicapp.db

import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.Movie



class MoviesDataSource(private val movieDAO: MovieDAO): IMoviesDataSource {

    companion object {
        private var mInstance: MoviesDataSource? = null

        fun getInstance(movieDAO: MovieDAO): MoviesDataSource{
            if(mInstance == null)
                mInstance = MoviesDataSource(movieDAO)
            return mInstance!!
        }
    }


    override val allMovies: Flowable<List<Movie>>
        get() = movieDAO.allMovies

    override fun getMovieById(id: Int): Flowable<Movie> {
        return movieDAO.getMovieById(id)
    }

    override fun insertAll(movies: List<Movie>) {
        movieDAO.insertMovies(movies)
    }

    override fun deleteAll() {
        movieDAO.deleteAllMovies()
    }






}