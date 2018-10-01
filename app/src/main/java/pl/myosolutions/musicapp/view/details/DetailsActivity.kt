package pl.myosolutions.musicapp.view.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.databinding.ActivityDetailsBinding
import pl.myosolutions.musicapp.http.MoviesAPI
import pl.myosolutions.musicapp.model.Movie
import pl.myosolutions.musicapp.view.main.GlideApp
import pl.myosolutions.musicapp.viewmodel.DetailsViewModel

class DetailsActivity : AppCompatActivity() {

    companion object {

        private val MOVIE_ID_KEY: String? = "MOVIE_ID_ARG"

        fun getIntent(packageClass: Context, movieId: Int): Intent {
            return Intent(packageClass, DetailsActivity::class.java).putExtra(MOVIE_ID_KEY, movieId)
        }
    }


    private lateinit var binding: ActivityDetailsBinding

    private lateinit var viewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpMovieObserver()
        viewModel.loadMovie(intent.extras.getInt(MOVIE_ID_KEY,0))

    }

    private fun setUpMovieObserver() {
        val moviesObserver = Observer<Movie> { movie ->
            binding.movie = movie
            GlideApp.with(this@DetailsActivity)
                    .load(MoviesAPI.BACKDROP_URL+ (movie?.backdrop_path ?: null))
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.backgroundImage)
        }

        viewModel.currentMovie!!.observe(this, moviesObserver)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.dispose()
    }
}
