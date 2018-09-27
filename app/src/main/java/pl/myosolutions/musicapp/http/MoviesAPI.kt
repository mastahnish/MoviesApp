package pl.myosolutions.musicapp.http

import com.google.gson.GsonBuilder
import io.reactivex.Flowable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



object MoviesAPI {


    var API_BASE_URL: String = "https://api.themoviedb.org/"
    const val API_KEY: String = "d9eb3dc80a4a233517342f4a6b622e3f"

    interface MovieService {

        @GET("/3/movie/now_playing")
        fun getMovies(@Query("api_key") apiKey: String, @Query("page") page: Int): Flowable<MovieResponse>


        companion object {
            fun create(): MovieService {

                val gson = GsonBuilder()
                        .setLenient()
                        .create()


                var retrofit = Retrofit.Builder()
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(API_BASE_URL)
                        .build()

                var movieService = retrofit.create(MovieService::class.java)

                return movieService
            }
        }
    }


}