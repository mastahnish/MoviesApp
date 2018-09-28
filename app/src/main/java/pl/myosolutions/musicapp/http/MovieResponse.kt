package pl.myosolutions.musicapp.http

import pl.myosolutions.musicapp.model.Movie

data class MovieResponse(val results: List<Movie>, val page: Int, val total_results: Int, val total_pages: Int)



