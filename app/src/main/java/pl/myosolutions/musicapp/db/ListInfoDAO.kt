package pl.myosolutions.musicapp.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import pl.myosolutions.musicapp.model.ListInfo

@Dao
interface ListInfoDAO {

    @get:Query("SELECT * FROM list_info")
    val listInfo: LiveData<ListInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInfo(info: ListInfo)


    @Query("DELETE FROM list_info")
    fun deleteInfo()

}