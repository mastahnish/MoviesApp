package pl.myosolutions.musicapp.view.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_main.*
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.databinding.FragmentMainBinding
import pl.myosolutions.musicapp.model.ListInfo
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.view.main.list.ILoadMore
import pl.myosolutions.musicapp.view.main.list.MoviesAdapter
import pl.myosolutions.musicapp.viewmodel.MainViewModel
import java.util.*

class MainFragment : Fragment(), ILoadMore, MoviesAdapter.MovieClickCallback {

    var moviesData: MutableList<Movie?> = ArrayList()
    var listInfo: ListInfo? = null
    var adapter: MoviesAdapter? = null

    private lateinit var binding: FragmentMainBinding
    private lateinit var mViewModel: MainViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        mViewModel = ViewModelProviders.of(this)
                .get(MainViewModel::class.java)


        initializeRecyclerView()
        setupObserversForViewModel()
        loadMovies(1)


        return binding.root
    }


    private fun setupObserversForViewModel() {

        val listInfoObserver = Observer<ListInfo> { info ->
            Log.d("MoviesApp", "listInfo observed \n ${info.toString()}")
            listInfo = info
        }

        mViewModel.listInfo.observe(this, listInfoObserver)


        val moviesObserver = Observer<List<Movie>> { newBatch ->

            when {
                adapter == null -> {
                    Log.i("MoviesApp", "when 1")

                    moviesData.clear()
                    newBatch?.toMutableList()?.let { moviesData.addAll(it) }

                    adapter = MoviesAdapter(binding.recyclerView, this.activity, moviesData, this)
                    binding.recyclerView.adapter = adapter
                    adapter?.setLoadMoreListener(this@MainFragment)

                }
                listInfo?.page != null && ((listInfo?.page)!! > 1) -> {
                    Log.i("MoviesApp", "when 2")

                    moviesData.removeAt(moviesData.size - 1)

                    newBatch?.size?.let {
                        adapter ?. removeLoadingItem (it)
                        moviesData.clear()
                        moviesData.addAll(newBatch.toMutableList())
                        adapter?.addNewMovies(listInfo?.page!!)
                    }

                }
                else -> {
                    Log.i("MoviesApp", "when else")
                    moviesData.clear()
                    newBatch?.toMutableList()?.let { moviesData.addAll(it) }

                    adapter?.notifyDataSetChanged()
                }
            }

            Log.d("MoviesApp", "moviesList observed \n moviesSize: ${moviesData.size} | totalResults: ${listInfo?.totalResults}")

        }

        mViewModel.movies.observe(this, moviesObserver)
    }


    private fun initializeRecyclerView() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = GridLayoutManager(this.context, 2, GridLayoutManager.VERTICAL, false)

    }


    override fun onLoadMore() {
        if (listInfo != null) {
            loadMore(listInfo!!)
        }
    }


    private fun loadMore(result: ListInfo) {
        Log.d("MoviesApp#loadMore", "current MoviesData Size: ${moviesData.size} | currentPage: ${result.page}")

        if (moviesData.size < result.totalResults!!) {

            recycler_view.post {
                moviesData.add(null)
                adapter?.notifyItemInserted(moviesData.size - 1)

                var newPage = result.page?.plus(1)!!
                Log.d("MoviesApp#loadMore", "newPage: $newPage")

                loadMovies(newPage)
            }


        } else {
            Toast.makeText(this.context, "That's current repertuar (${result.totalResults} movies)", Toast.LENGTH_SHORT).show()
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
        Log.d("MoviesApp", movie.toString())
        (this.activity as MainActivity).showMovie(movie)
    }


}
