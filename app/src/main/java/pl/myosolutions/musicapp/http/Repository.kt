package pl.myosolutions.musicapp.http

import android.util.Log
import ru.gildor.coroutines.retrofit.getOrThrow


class Repository {
    suspend fun getMovies() {
        try {
            val postListResponse = MoviesAPI.movieService.getMovies().awaitResult().getOrThrow()
            for(movie in postListResponse.data)
                Log.d("MoviesApp", movie.toString() )
        }
        catch (e: Exception) {
            Log.d("MoviesApp", e.message)        }
    }

   /* fun getPostList(): MutableLiveData<Resource<List<Post>>> {
        val result = MutableLiveData<Resource<List<Post>>>()
        result.setValue(Resource.loading(null))
        val client = MoviesAPI.movieService
        launch {
            try {
                val postListResponse = client.getMovies().awaitResult().getOrThrow()
                withContext(UI) { result.value = Resource.success(postListResponse.data) }
            }
            catch (e: Exception) {
                withContext(UI) { result.value = Resource.error("Unable to get post list", null) }
            }
        }
        return result
    }*/

}




