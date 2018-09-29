package pl.myosolutions.musicapp.view.main.list

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.loading_layout.view.*
import kotlinx.android.synthetic.main.movie_item_layout.view.*
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.http.MoviesAPI.POSTER_URL
import pl.myosolutions.musicapp.model.Movie


internal class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var progressBar = itemView.progress_bar
}

internal class MovieItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var itemBackground = itemView.item_background
}

class MoviesAdapter(recyclerView: RecyclerView, var activity: Activity, private var items: List<Movie?>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM: Int = 0
    private val VIEW_TYPE_LOADING: Int = 1

    internal var loadMoreListener: ILoadMore? = null
    internal var isLoading: Boolean = false
    internal var visibleThreshold = 1
    internal var lastVisibleItem: Int = 0
    internal var totalItemCount: Int = 0


    init {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(activity).inflate(R.layout.movie_item_layout, parent, false)
            MovieItemViewHolder(view)
        } else /*if (viewType == VIEW_TYPE_LOADING)*/ {
            val view = LayoutInflater.from(activity).inflate(R.layout.loading_layout, parent, false)
            LoadingViewHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (items?.get(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieItemViewHolder) {

            val item = items?.get(position) as Movie
            val url = POSTER_URL + item.poster_path

            Glide.with(activity)
                    .load(url)
                    .into(holder.itemBackground)

        } else if (holder is LoadingViewHolder) {

            holder.progressBar.isIndeterminate = true
        }
    }


    fun setLoaded() {
        isLoading = false
    }

    fun setLoadMoreListener(iLoadMore: ILoadMore) {
        this.loadMoreListener = iLoadMore
    }

    fun removeLoadingItem() {
//        items = movies.toMutableList()
        notifyItemRemoved(items!!.size)
        notifyItemRangeChanged(items!!.size, 20)
    }

    fun addNewMovies(page: Int/*, movies: List<Movie>*/) {
//        items = movies.toMutableList()
        notifyItemRangeInserted(page*items!!.size, items!!.size)
        setLoaded()
    }


}