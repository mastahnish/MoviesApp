package pl.myosolutions.musicapp.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import io.reactivex.annotations.NonNull

@Entity(tableName = "movies")
class Movie{
        @NonNull
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name="id")
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
        return StringBuilder(title)
                .append("\n\n")
                .append(overview)
                .append("\n\n")
                .append(poster_path)
                .append("\n\n")
                .append(backdrop_path)
                .append("\n\n\n")
                .toString()
    }


}