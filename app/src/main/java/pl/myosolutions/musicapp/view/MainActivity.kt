package pl.myosolutions.musicapp.view

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.db.MovieDatabase
import pl.myosolutions.musicapp.db.MovieRepository
import pl.myosolutions.musicapp.db.MoviesDataSource
import pl.myosolutions.musicapp.http.MoviesAPI
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.utils.NetworkUtils.isConnected


class MainActivity : AppCompatActivity() {

    private var compositeDisposable: CompositeDisposable? = null

    private var movieRepository: MovieRepository? = null

    private val movieApiSevice by lazy {
        MoviesAPI.MovieService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val movieDatabase = MovieDatabase.getInstance(this)
        movieRepository = MovieRepository.getInstance(MoviesDataSource.getInstance(movieDatabase.movieDAO()))
        compositeDisposable = CompositeDisposable()

        loadMovies()
    }




    private fun loadMovies() {
        if(isConnected(this)) loadFromExternalServer() else loadFromLocalDb()
    }

    private fun loadFromLocalDb() {

        val disposable = movieRepository!!.allMovies
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            propagateResults(result)
                        },
                        { error ->
                            Log.d("MoviesApp", error.message)
                            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        }
                )

        compositeDisposable!!.add(disposable)


    }

    private fun loadFromExternalServer() {

        val disposable =  movieApiSevice.getMovies(MoviesAPI.API_KEY,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            insertToDB(result.results)
                            propagateResults(result.results)
                        },
                        { error ->
                            Log.d("MoviesApp", error.message)
                            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        }
                )

        compositeDisposable!!.add(disposable)


    }

    private fun insertToDB(results: List<Movie>) {
        launch{
            movieRepository!!.deleteAll()
            movieRepository!!.insertAll(results)
        }
    }

    private fun propagateResults(results: List<Movie>) {
        Log.d("MoviesApp", results.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        var searchItem: MenuItem? = menu.findItem(R.id.action_search)
        var searchManager: SearchManager = this@MainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var searchView: SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.getActionView() as SearchView
        }
        if (searchView != null) {
            searchView!!.setSearchableInfo(searchManager.getSearchableInfo(this@MainActivity.componentName))
        }


        return super.onCreateOptionsMenu(menu)
    }


    override fun onPause() {
        super.onPause()
        compositeDisposable?.dispose()
    }
}
