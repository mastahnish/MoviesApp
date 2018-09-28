package pl.myosolutions.musicapp.db

import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.ListInfo

class ListInfoRepository (private val dataSource: IListInfoDataSource): IListInfoDataSource{

    companion object {
        private var mInstance: ListInfoRepository? = null

        fun getInstance(dataSource: IListInfoDataSource): ListInfoRepository{
            if(mInstance == null)
                mInstance = ListInfoRepository(dataSource)
            return mInstance!!
        }
    }

    override val listInfo: Flowable<ListInfo>
        get() = dataSource.listInfo

    override fun insertInfo(info: ListInfo) {
        dataSource.insertInfo(info)
    }

    override fun updateInfo(info: ListInfo) {
        dataSource.updateInfo(info)
    }

    override fun deletInfo() {
       dataSource.deletInfo()
    }
}