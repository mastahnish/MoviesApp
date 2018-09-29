package pl.myosolutions.musicapp.db

import android.arch.lifecycle.LiveData
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

    override val listInfo: LiveData<ListInfo>
        get() = listInfoDAO.listInfo

    override fun insertInfo(info: ListInfo) {
       listInfoDAO.insertInfo(info)
    }

    override fun deletInfo() {
       listInfoDAO.deleteInfo()
    }


}