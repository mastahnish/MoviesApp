package pl.myosolutions.musicapp.db

import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.ListInfo

interface IListInfoDataSource {

    val listInfo: Flowable<ListInfo>

    fun insertInfo(info: ListInfo)

    fun updateInfo(info: ListInfo)

    fun deletInfo()


}