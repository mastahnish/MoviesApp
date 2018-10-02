package pl.myosolutions.musicapp.view.search


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.databinding.FragmentSearchBinding
import pl.myosolutions.musicapp.model.ListInfo
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.view.main.MainActivity
import pl.myosolutions.musicapp.view.main.list.ILoadMore
import pl.myosolutions.musicapp.view.main.list.MoviesAdapter
import pl.myosolutions.musicapp.viewmodel.SearchViewModel
import java.util.*


class SearchFragment : Fragment(), ILoadMore, MoviesAdapter.MovieClickCallback {
    companion object {

        private val QUERY_TEXT_KEY: String? = "QUERY_TEXT_KEY"

        fun getInstance(query: String): SearchFragment {
            val frag = SearchFragment()
            val args = Bundle()
            args.putString(QUERY_TEXT_KEY, query)
            frag.arguments = args
            return frag
        }
    }

    private var moviesData: MutableList<Movie?> = ArrayList()
    private var listInfo: ListInfo? = null
    private var adapter: MoviesAdapter? = null

    private lateinit var mViewModel: SearchViewModel
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        mViewModel = ViewModelProviders.of(this)
                .get(SearchViewModel::class.java)

        val query = arguments?.getString(QUERY_TEXT_KEY)
        mViewModel.currentQuery = query

        initializeRecyclerView()
        setupObserversForViewModel()
        loadMovies(1)
        return binding.root
    }

    private fun initializeRecyclerView() {

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = GridLayoutManager(this.context, 2, GridLayoutManager.VERTICAL, false)

    }


    private fun setupObserversForViewModel() {

        val listInfoObserver = Observer<ListInfo> { info ->
            listInfo = info

            if(listInfo !=null){
                val totalResults = listInfo?.totalResults
                if (totalResults!! < 4) {
                    notifyAllResultsDisplay(totalResults)
                }
            }

        }

        mViewModel.listInfo.observe(this, listInfoObserver)


        val moviesObserver = Observer<List<Movie>> { newBatch ->

            when {
                adapter == null -> {
                    initializeAdapter(newBatch)
                }
                listInfo?.page != null && ((listInfo?.page)!! > 1) -> {
                    appendNewData(newBatch)
                }
                else -> {
                    propagateInitialData(newBatch)
                }
            }

            Log.d("MoviesApp", "moviesList observed \n moviesSize: ${moviesData.size} | totalResults: ${listInfo?.totalResults}")

        }

        mViewModel.movies.observe(this, moviesObserver)
    }



    private fun initializeAdapter(newBatch: List<Movie>?) {
        moviesData.clear()
        newBatch?.toMutableList()?.let { moviesData.addAll(it) }
        adapter = MoviesAdapter(binding.recyclerView, this.activity, moviesData, this)
        binding.recyclerView.adapter = adapter
        adapter?.setLoadMoreListener(this@SearchFragment)
    }

    private fun appendNewData(newBatch: List<Movie>?) {
        moviesData.removeAt(moviesData.size - 1)

        newBatch?.size?.let {
            adapter?.removeLoadingItem(it)
            moviesData.clear()
            moviesData.addAll(newBatch.toMutableList())
            adapter?.addNewMovies(listInfo?.page!!)
        }
    }

    private fun propagateInitialData(newBatch: List<Movie>?) {
        moviesData.clear()
        newBatch?.toMutableList()?.let { moviesData.addAll(it) }
        adapter?.notifyDataSetChanged()
    }



    override fun onLoadMore() {
        if (listInfo != null) {
            loadMore(listInfo!!)
        }
    }


    private fun loadMore(result: ListInfo) {

        if (moviesData.size < result.totalResults!!) {
            recycler_view.post {
                moviesData.add(null)
                adapter?.notifyItemInserted(moviesData.size - 1)

                val newPage = result.page?.plus(1)!!
                loadMovies(newPage)
            }

        } else {

            notifyAllResultsDisplay(result.totalResults)
        }
    }

    private fun notifyAllResultsDisplay(totalResults: Int?) {
        if (totalResults != null) {
            val snackbar = Snackbar.make(binding.recyclerView, "That's all we can find ($totalResults movies)", Snackbar.LENGTH_LONG)
            snackbar.setAction(getString(R.string.OK)) { snackbar.dismiss() }
            snackbar.show()
        }
    }


    private fun loadMovies(page: Int) {
        mViewModel.loadMovies(page)
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.dispose()
    }

    override fun onClick(movie: Movie) {
        (this.activity as MainActivity).showMovie(movie)
    }


}
