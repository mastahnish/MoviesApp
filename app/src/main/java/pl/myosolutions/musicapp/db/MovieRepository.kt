package pl.myosolutions.musicapp.db

import android.arch.lifecycle.LiveData
import pl.myosolutions.musicapp.model.Movie
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MovieRepository (private val dataSource: IMoviesDataSource): IMoviesDataSource{

    companion object {
        private var mInstance: MovieRepository? = null

        fun getInstance(dataSource: IMoviesDataSource): MovieRepository{
            if(mInstance == null)
                mInstance = MovieRepository(dataSource)
            return mInstance!!
        }
    }


    private val executor: Executor = Executors.newSingleThreadExecutor()

    override val allMovies: LiveData<List<Movie>>
        get() = dataSource.allMovies

    override fun getMovieById(id: Int): Movie {
        return dataSource.getMovieById(id)
    }

    override fun insertAll(movies: List<Movie>) {
        executor.execute { dataSource.insertAll(movies) }
    }

    override fun deleteAll() {
        executor.execute{dataSource.deleteAll()}
    }



}