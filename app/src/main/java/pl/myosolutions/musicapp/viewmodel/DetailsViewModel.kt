package pl.myosolutions.musicapp.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pl.myosolutions.musicapp.db.MovieDatabase
import pl.myosolutions.musicapp.db.MovieRepository
import pl.myosolutions.musicapp.db.MoviesDataSource
import pl.myosolutions.musicapp.model.Movie

class DetailsViewModel(application: Application)
    : AndroidViewModel(application) {


    private var context: Context = application.applicationContext
    private var movieRepository: MovieRepository?
    var currentMovie: MutableLiveData<Movie>? = MutableLiveData()
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()


    init{
        var movieDatabase: MovieDatabase = MovieDatabase.getInstance(context)
        movieRepository = MovieRepository.getInstance(MoviesDataSource.getInstance(movieDatabase.movieDAO()))
    }

    fun loadMovie(id: Int) {
        loadMovieFromLocalDb(id)
    }

    private fun loadMovieFromLocalDb(id: Int) {
        val disposable = movieRepository!!.getMovieById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            Log.d("MoviesApp", "loadMovieFromLocalDb $result")
                            currentMovie?.postValue(result)
                        },
                        { error ->
                            currentMovie?.postValue(Movie.getEmptyMovie())
                            Log.d("MoviesApp", error.message)
                            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                        }
                )

        compositeDisposable.add(disposable)
    }


    fun dispose(){
        compositeDisposable.dispose()
    }
}