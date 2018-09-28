package pl.myosolutions.musicapp.db

import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.Movie

class MovieRepository (private val dataSource: IMoviesDataSource): IMoviesDataSource{

    companion object {
        private var mInstance: MovieRepository? = null

        fun getInstance(dataSource: IMoviesDataSource): MovieRepository{
            if(mInstance == null)
                mInstance = MovieRepository(dataSource)
            return mInstance!!
        }
    }

    override val allMovies: Flowable<List<Movie>>
        get() = dataSource.allMovies

    override fun getMovieById(id: Int): Flowable<Movie> {
        return dataSource.getMovieById(id)
    }

    override fun insertAll(movies: List<Movie>) {
       dataSource.insertAll(movies)
    }

    override fun deleteAll() {
        dataSource.deleteAll()
    }



}