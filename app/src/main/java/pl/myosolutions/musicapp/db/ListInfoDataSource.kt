package pl.myosolutions.musicapp.db

import io.reactivex.Flowable
import pl.myosolutions.musicapp.model.ListInfo

class ListInfoDataSource (private val listInfoDAO: ListInfoDAO): IListInfoDataSource{


    companion object {
        private var mInstance: ListInfoDataSource? = null

        fun getInstance(listInfoDAO: ListInfoDAO): ListInfoDataSource{
            if(mInstance == null)
                mInstance = ListInfoDataSource(listInfoDAO)
            return mInstance!!
        }
    }

    override val listInfo: Flowable<ListInfo>
        get() = listInfoDAO.listInfo

    override fun insertInfo(info: ListInfo) {
       listInfoDAO.insertInfo(info)
    }

    override fun updateInfo(info: ListInfo) {
       listInfoDAO.updateInfo(info)
    }

    override fun deletInfo() {
       listInfoDAO.deleteInfo()
    }


}