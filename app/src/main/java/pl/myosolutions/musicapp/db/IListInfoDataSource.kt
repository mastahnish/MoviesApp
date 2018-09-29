package pl.myosolutions.musicapp.db

import android.arch.lifecycle.LiveData
import pl.myosolutions.musicapp.model.ListInfo

interface IListInfoDataSource {

    val listInfo: LiveData<ListInfo>

    fun insertInfo(info: ListInfo)

    fun deletInfo()


}