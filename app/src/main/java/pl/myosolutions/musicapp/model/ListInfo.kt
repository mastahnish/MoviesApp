package pl.myosolutions.musicapp.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
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
    var total_results: Int?= 0

    @ColumnInfo(name="total_pages")
    var total_pages: Int?= 0


    constructor(){}

    constructor(page: Int?, total_results: Int?, total_pages: Int?) {
        this.page = page
        this.total_results = total_results
        this.total_pages = total_pages
    }


    override fun toString(): String {
        return StringBuilder(page!!)
                .append("\n")
                .append(total_results)!!
                .append("\n")
                .append(total_pages!!)
                .toString()
    }


}