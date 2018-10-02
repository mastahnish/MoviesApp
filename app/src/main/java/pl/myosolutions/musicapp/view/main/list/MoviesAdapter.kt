package pl.myosolutions.musicapp.view.main.list

import android.databinding.DataBindingUtil
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.loading_layout.view.*
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.databinding.MovieItemLayoutBinding
import pl.myosolutions.musicapp.http.MoviesAPI.POSTER_URL
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.view.main.GlideApp


internal class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var progressBar = itemView.progress_bar
}

internal class MovieItemViewHolder(itemViewBinding: MovieItemLayoutBinding) : RecyclerView.ViewHolder(itemViewBinding.root) {
    var binding = itemViewBinding
}

const val MOVIE_TYPE: Int = 0
const val LOADING_TYPE: Int = 1

class MoviesAdapter(recyclerView: RecyclerView, private var activity: FragmentActivity?, private var items: List<Movie?>?, private val clickCallback: MovieClickCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    internal var loadMoreListener: ILoadMore? = null
    internal var isLoading: Boolean = false
    internal var visibleThreshold = 6
    internal var lastVisibleItem: Int = 0
    internal var totalItemCount: Int = 0


    init {
        val linearLayoutManager = recyclerView.layoutManager as GridLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (loadMoreListener != null)
                        loadMoreListener!!.onLoadMore()

                    isLoading = true
                }
            }
        })

        linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                return when (getItemViewType(position)) {
                    MOVIE_TYPE -> 1
                    LOADING_TYPE -> 2
                    else -> -1
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == MOVIE_TYPE) {
            val binding = DataBindingUtil
                    .inflate<MovieItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.movie_item_layout,
                            parent, false)
            binding.callback = clickCallback
            MovieItemViewHolder(binding)
        } else {
            val view = LayoutInflater.from(activity).inflate(R.layout.loading_layout, parent, false)
            LoadingViewHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (items?.get(position) == null) LOADING_TYPE else MOVIE_TYPE
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieItemViewHolder) {

            val item = items?.get(position) as Movie
            holder.binding.movie = item

            val url = POSTER_URL + item.poster_path

            GlideApp.with(activity)

                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.grey_dark)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(holder.binding.itemBackground)


        } else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }


    private fun setLoaded() {
        isLoading = false
    }

    fun setLoadMoreListener(iLoadMore: ILoadMore) {
        this.loadMoreListener = iLoadMore
    }

    fun removeLoadingItem(newBatchSize: Int) {
        notifyItemRemoved(items!!.size)
        notifyItemRangeChanged(items!!.size, newBatchSize)
    }

    fun addNewMovies(page: Int) {
        notifyItemRangeInserted(page * items!!.size, items!!.size)
        setLoaded()
    }


    interface MovieClickCallback {
        fun onClick(movie: Movie)
    }

}