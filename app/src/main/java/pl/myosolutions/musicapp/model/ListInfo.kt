package pl.myosolutions.musicapp.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import io.reactivex.annotations.NonNull

@Entity(tableName = "list_info")
class ListInfo{
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id: Int=0

    @ColumnInfo(name="page:")
    var page: Int?= 0

    @ColumnInfo(name="total_results")
    @SerializedName("total_results")
    var totalResults: Int?= 0

    @ColumnInfo(name="total_pages")
    @SerializedName("total_pages")
    var totalPages: Int?= 0


    constructor(){}

    constructor(page: Int?, total_results: Int?, total_pages: Int?) {
        this.page = page
        this.totalResults = total_results
        this.totalPages = total_pages
    }


    override fun toString(): String {
        return StringBuilder("\npage: $page")
                .append("\n")
                .append("total_results: $totalResults")
                .append("\n")
                .append("total_pages: $totalPages")
                .toString()
    }


}