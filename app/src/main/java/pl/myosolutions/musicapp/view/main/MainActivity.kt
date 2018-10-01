package pl.myosolutions.musicapp.view.main


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.view.details.DetailsActivity


class MainActivity : AppCompatActivity() {

    lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fragmentManager = supportFragmentManager
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container,
                        MainFragment(), MainFragment::class.java.simpleName).commit()

    }

    fun showMovie(movie: Movie) {
        startActivityForResult(DetailsActivity.getIntent(this, movie.id), 1)
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        var searchItem: MenuItem? = menu.findItem(R.id.action_search)
        var searchManager: SearchManager = this@MainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var searchView: SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(this@MainActivity.componentName))


        return super.onCreateOptionsMenu(menu)
    }
}
