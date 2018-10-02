package pl.myosolutions.musicapp.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pl.myosolutions.musicapp.db.*
import pl.myosolutions.musicapp.http.MovieResponse
import pl.myosolutions.musicapp.http.MoviesAPI
import pl.myosolutions.musicapp.model.ListInfo
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.utils.NetworkUtils

class SearchViewModel(application: Application)
    : AndroidViewModel(application) {


    private var context: Context = application.applicationContext
    private var movieRepository: MovieRepository
    private var listInfoRepository: ListInfoRepository
    var movies: LiveData<List<Movie>>
    var listInfo: LiveData<ListInfo>
    var currentQuery: String? = ""
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val movieApiService by lazy {
        MoviesAPI.MovieService.create()
    }


    init{
        val movieDatabase: MovieDatabase = MovieDatabase.getInstance(application.applicationContext)
        movieRepository = MovieRepository.getInstance(MoviesDataSource.getInstance(movieDatabase.movieDAO()))
        listInfoRepository = ListInfoRepository.getInstance(ListInfoDataSource.getInstance(movieDatabase.listInfoDAO()))
        movieRepository.deleteAll()
        listInfoRepository.deletInfo()

        movies = movieRepository.allMovies
        listInfo = listInfoRepository.listInfo

    }

    fun loadMovies(page: Int) {
        if (NetworkUtils.isConnected(context)) loadFromExternalServer(page) else loadFromLocalDb()
    }

    private fun loadFromExternalServer(page: Int) {

        if(page == 1){
            movieRepository.deleteAll()
        }

        val disposable = movieApiService.getSearchResults(MoviesAPI.API_KEY, currentQuery, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            insertToDB(result)
                        },
                        { error ->
                            Log.d("MoviesApp", error.message)
                            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                        }
                )

        compositeDisposable.add(disposable)


    }



    private fun loadFromLocalDb(){
        movies = movieRepository.allMovies
        listInfo = listInfoRepository.listInfo
    }



    private fun insertToDB(response: MovieResponse) {
        insertInfo(ListInfo(1, response.page, response.total_results, response.total_pages))
        insertMovies(response.results)
    }

    private fun insertInfo(info: ListInfo) {
        listInfoRepository.insertInfo(info)
    }

    private fun insertMovies(movies: List<Movie>) {
        movieRepository.insertAll(movies)

    }


    fun dispose(){
        compositeDisposable.dispose()
    }

}