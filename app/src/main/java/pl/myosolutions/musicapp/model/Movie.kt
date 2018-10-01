package pl.myosolutions.musicapp.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import io.reactivex.annotations.NonNull

@Entity(tableName = "movies")
class Movie{
        @NonNull
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name="id")
        var uuid: Int=0

        @ColumnInfo(name="movie_id")
        var id: Int=0

        @ColumnInfo(name="title")
        var title: String?=null

        @ColumnInfo(name="overview")
        var overview: String?=null

        @ColumnInfo(name="poster_path")
        var poster_path: String?=null

        @ColumnInfo(name="backdrop_path")
        var backdrop_path: String?=null

        constructor(){}

    override fun toString(): String {
        return StringBuilder(uuid)
                .append("\n")
                .append(id)
                .append("\n")
                .append(title)
                .append("\n")
                .append(overview)
                .append("\n")
                .append(poster_path)
                .append("\n")
                .append(backdrop_path)
                .append("\n\n")
                .toString()
    }

        companion object {
            fun getEmptyMovie(): Movie{
                    var movie: Movie = Movie()
                    movie.overview = "No data"
                    movie.title = "No such movie"
                    return movie
            }
        }


}