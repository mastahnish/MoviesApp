package pl.myosolutions.musicapp.http

import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object MoviesAPI {

    var API_BASE_URL: String = "https://api.themoviedb.org/3/"
    const val API_KEY: String = "d9eb3dc80a4a233517342f4a6b622e3f"

    var retrofit  = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    var movieService = retrofit.create<MovieService>(MovieService::class.java!!)

    interface MovieService {

        @GET("/movie/now_playing?api_key="+API_KEY)
        fun getMovies(): Deferred<Response<MovieResponse>>
    }


}