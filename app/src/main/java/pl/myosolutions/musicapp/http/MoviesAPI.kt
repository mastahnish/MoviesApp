package pl.myosolutions.musicapp.http

import com.google.gson.GsonBuilder
import io.reactivex.Flowable
import pl.myosolutions.musicapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


object MoviesAPI {


    var API_BASE_URL: String = "https://api.themoviedb.org/"
    const val API_KEY: String = BuildConfig.Movies_API_Key

    private const val IMAGE_DOMAIN: String = "https://image.tmdb.org/t/p"
    const val POSTER_URL: String = "$IMAGE_DOMAIN/w500/"
    const val BACKDROP_URL: String = "$IMAGE_DOMAIN/w780"
    const val API_FIELD_KEY: String = "api_key"
    const val PAGE_FIELD_KEY: String = "page"
    const val QUERY_FIELD_KEY: String = "query"

    interface MovieService {

        @GET("/3/movie/now_playing/")
        fun getMovies(@Query(API_FIELD_KEY) apiKey: String, @Query(PAGE_FIELD_KEY) page: Int): Flowable<MovieResponse>

        @GET("/3/search/movie/")
        fun getSearchResults(@Query(API_FIELD_KEY) apiKey: String, @Query(QUERY_FIELD_KEY) query: String?, @Query(PAGE_FIELD_KEY) page: Int): Flowable<MovieResponse>

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