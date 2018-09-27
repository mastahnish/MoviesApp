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
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.http.MoviesAPI


class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null


    private val movieApiSevice by lazy {
        MoviesAPI.MovieService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        disposable = movieApiSevice.getMovies(MoviesAPI.API_KEY,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            for(movie in result.results)
                            Log.d("MoviesApp", movie.toString() ) },
                        { error ->
                            Log.d("MoviesApp", error.message)
                            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                )


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
        disposable?.dispose()
    }
}
