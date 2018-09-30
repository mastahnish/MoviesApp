package pl.myosolutions.musicapp.http

import pl.myosolutions.musicapp.model.Movie

data class MovieResponse(val results: List<Movie>, val page: Int, val total_results: Int, val total_pages: Int){
    override fun toString(): String {
        return StringBuilder("MovieResponse = \npage: $page")
                .append(" || ")
                .append("total_results: $total_results")
                .append(" || ")
                .append("total_pages: $total_pages")
                .append(" || ")
                .append("resultsSize: ${results.size}")
                .toString()
    }
}



