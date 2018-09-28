package pl.myosolutions.musicapp.view.main

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.launch
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.db.*
import pl.myosolutions.musicapp.http.MovieResponse
import pl.myosolutions.musicapp.http.MoviesAPI
import pl.myosolutions.musicapp.model.ListInfo
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.utils.NetworkUtils.isConnected
import pl.myosolutions.musicapp.view.main.list.ILoadMore
import pl.myosolutions.musicapp.view.main.list.MoviesAdapter


class MainActivity : AppCompatActivity(), ILoadMore{

    var movies: MutableList<Movie?> = ArrayList()
    lateinit var adapter:MoviesAdapter

    //TODO probably move to ViewModel
    private var compositeDisposable: CompositeDisposable? = null

    private var movieRepository: MovieRepository? = null
    private var listInfoRepository: ListInfoRepository? = null

    private val movieApiSevice by lazy {
        MoviesAPI.MovieService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initialize()
        loadMovies()
    }

    private fun initialize() {
        val movieDatabase = MovieDatabase.getInstance(this)
        movieRepository = MovieRepository.getInstance(MoviesDataSource.getInstance(movieDatabase.movieDAO()))
        listInfoRepository = ListInfoRepository.getInstance(ListInfoDataSource.getInstance(movieDatabase.listInfoDAO()))
        compositeDisposable = CompositeDisposable()

        recycler_view.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL,false);
        adapter = MoviesAdapter(recycler_view, this, movies)
        recycler_view.adapter = adapter
        adapter.setLoadMoreListener(this)

    }



    //TODO probably move to ViewModel
    private fun loadMovies() {
        if(isConnected(this)) loadFromExternalServer(1) else loadFromLocalDb()
    }

    private fun loadFromLocalDb() {

        val disposable = movieRepository!!.allMovies
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            propagateFirstResults(result)
                        },
                        { error ->
                            Log.d("MoviesApp", error.message)
                            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        }
                )

        compositeDisposable!!.add(disposable)


    }

    private fun loadFromExternalServer(page: Int) {

        val disposable =  movieApiSevice.getMovies(MoviesAPI.API_KEY,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            insertToDB(result)

                            if(page>1) appendResults(page, result.results)
                            else propagateFirstResults(result.results)
                        },
                        { error ->
                            Log.d("MoviesApp", error.message)
                            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        }
                )

        compositeDisposable!!.add(disposable)


    }


    private fun insertToDB(response: MovieResponse) {
        Log.d("MoviesApp", ListInfo(response.page, response.total_results, response.total_pages).toString())
        launch{
            if(response.page==1) {
                movieRepository!!.deleteAll()
                listInfoRepository!!.insertInfo(ListInfo(response.page,  response.total_results, response.total_pages))
            }else{
                listInfoRepository!!.updateInfo(ListInfo(response.page, response.total_results, response.total_pages))
            }

            movieRepository!!.insertAll(response.results)
        }
    }


    private fun appendResults(page: Int, results: List<Movie>) {
        Log.d("MoviesApp#appendResults", "resultsSize: ${results.size}  moviesSize: ${movies.size}")

        movies.removeAt(movies.size-1)
        adapter.removeLoadingItem()

        movies.addAll(results)
        adapter.addNewMovies(page)


    }

    private fun propagateFirstResults(results: List<Movie>) {
        Log.d("MoviesApp", "propagateFirstResults: ${results.size}")
        movies.addAll(results)
//        adapter.items = movies
        adapter.notifyDataSetChanged()
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
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this@MainActivity.componentName))
        }


        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        compositeDisposable!!.clear()
        super.onDestroy()

    }


    //TODO probably move to ViewModel
    override fun onLoadMore() {
        val disposable =  listInfoRepository!!.listInfo
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            loadMore(result)
                        },
                        { error ->
                            Log.d("MoviesApp", error.message)
                            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        }
                )

        compositeDisposable!!.add(disposable)
    }


    //TODO probably move to ViewModel
    private fun loadMore(result: ListInfo) {
        Log.d("MoviesApp#appendResults", "moviesSize: ${movies.size} | totalResults: ${result.totalResults}")

        if(movies.size < result.totalResults!!){
            movies.add(null)
            adapter.notifyItemInserted(movies.size-1)

            loadFromExternalServer(result.page?.plus(1)!!)

        }else{
            Toast.makeText(this, "Max data is reached (${result.totalResults}", Toast.LENGTH_SHORT).show()
        }
    }

}
