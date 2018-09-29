package pl.myosolutions.musicapp.view.main

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.model.ListInfo
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.view.main.list.ILoadMore
import pl.myosolutions.musicapp.view.main.list.MoviesAdapter
import pl.myosolutions.musicapp.viewmodel.MainViewModelKotlin
import java.util.*


class MainActivity : AppCompatActivity(), ILoadMore {

    var moviesData: MutableList<Movie?> = ArrayList()
    var listInfo: ListInfo? = null
    var adapter: MoviesAdapter? = null

    private lateinit var mViewModel: MainViewModelKotlin


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        mViewModel = ViewModelProviders.of(this)
                .get(MainViewModelKotlin::class.java)


        initializeRecyclerView()
        setupObserversForViewModel()
        loadMovies(1)
    }


    private fun setupObserversForViewModel() {
        val moviesObserver = Observer<List<Movie>> { movies ->

            Log.d("MoviesApp#appendResults", "moviesSize: ${moviesData.size} | totalResults: ${listInfo?.totalResults}")
            if (adapter == null) {
                moviesData.clear()
                moviesData.addAll(movies!!.toMutableList())
                adapter = MoviesAdapter(recycler_view, this@MainActivity, moviesData)
                recycler_view.adapter = adapter
                adapter!!.setLoadMoreListener(this@MainActivity)

            } else if (listInfo?.page!! > 1) {
                moviesData.removeAt(moviesData.size - 1)
                adapter!!.removeLoadingItem()

                moviesData.clear()
                moviesData.addAll(movies!!.toMutableList())
                adapter!!.addNewMovies(listInfo?.page!!)
            } else {
                moviesData.clear()
                moviesData.addAll(movies!!.toMutableList())
                adapter!!.notifyDataSetChanged()
            }
        }

        mViewModel.movies.observe(this, moviesObserver)


        val listInfoObserver = Observer<ListInfo> { info ->
            listInfo = info
        }

        mViewModel.listInfo.observe(this, listInfoObserver)

    }


    private fun initializeRecyclerView() {

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

    }

    //TODO probably move to ViewModel
    override fun onLoadMore() {
        loadMore(listInfo!!)
        /*  val disposable = listInfoRepository!!.listInfo.
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(
                          { result ->
                              //                            loadMore(result)
                          },
                          { error ->
                              Log.d("MoviesApp", error.message)
                              Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                          }
                  )

          compositeDisposable.add(disposable)*/
    }


    //TODO probably move to ViewModel
    private fun loadMore(result: ListInfo) {
        Log.d("MoviesApp#appendResults", "moviesSize: ${moviesData.size} | totalResults: ${result.totalResults}")

        if (moviesData.size < result.totalResults!!) {
            moviesData.add(null)
            adapter!!.notifyItemInserted(moviesData.size - 1)

            loadMovies(result.page?.plus(1)!!)

        } else {
            Toast.makeText(this, "Max data is reached (${result.totalResults}", Toast.LENGTH_SHORT).show()
        }
    }

/*
    private fun appendResults(page: Int, results: List<Movie>) {
        Log.d("MoviesApp#appendResults", "resultsSize: ${results.size}  moviesSize: ${R.string.movies.size}")

        movies.removeAt(R.string.movies.size - 1)
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

*/


    private fun loadMovies(page: Int) {
        mViewModel.loadMovies(page)
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


}
