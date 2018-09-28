package pl.myosolutions.musicapp.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.ListInfo

@Dao
interface ListInfoDAO {

    @get:Query("SELECT * FROM list_info")
    val listInfo: Flowable<ListInfo>

    @Insert
    fun insertInfo(info: ListInfo)

    @Update
    fun updateInfo(info: ListInfo)

    @Query("DELETE FROM list_info")
    fun deleteInfo()

}