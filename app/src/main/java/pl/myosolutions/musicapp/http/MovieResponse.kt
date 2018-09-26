package pl.myosolutions.musicapp.http

import pl.myosolutions.musicapp.model.Movie

data class MovieResponse(val data: List<Movie>): BaseApiResponse(){}

abstract class BaseApiResponse {
    var status: Int = 0
    var message: String? = null
}


