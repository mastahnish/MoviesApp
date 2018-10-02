package pl.myosolutions.musicapp.view.main


import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import kotlinx.android.synthetic.main.activity_main.*
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.view.details.DetailsActivity
import pl.myosolutions.musicapp.view.search.SearchFragment


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
                        MainFragment(), MainFragment::class.java.simpleName)
                .commit()

    }

    fun showMovie(movie: Movie) {
        startActivityForResult(DetailsActivity.getIntent(this, movie.id), 1)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)

        var menuItem = menu.findItem(R.id.action_search)

        (menuItem.actionView as SearchView).apply {
            this.setOnQueryTextListener(queryTextListener)
        }

        return super.onCreateOptionsMenu(menu)
    }


    private var queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {

            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            if(newText.isNotEmpty()){
                val fragment = SearchFragment.getInstance(newText)

                var searchTag = SearchFragment::class.java.simpleName
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment, searchTag)
                        .commit()
            }else{
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,  MainFragment(), MainFragment::class.java.simpleName)
                        .commit()
            }

            return true
        }
    }
}
