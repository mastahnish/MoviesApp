package pl.myosolutions.musicapp.db

import android.arch.lifecycle.LiveData
import pl.myosolutions.musicapp.model.ListInfo
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ListInfoRepository (private val dataSource: IListInfoDataSource): IListInfoDataSource{

    companion object {
        private var mInstance: ListInfoRepository? = null

        fun getInstance(dataSource: IListInfoDataSource): ListInfoRepository{
            if(mInstance == null)
                mInstance = ListInfoRepository(dataSource)
            return mInstance!!
        }
    }

    private val exector: Executor = Executors.newSingleThreadExecutor()

    override val listInfo: LiveData<ListInfo>
        get() = dataSource.listInfo

    override fun insertInfo(info: ListInfo) {
        exector.execute {dataSource.insertInfo(info)}

    }

    override fun deletInfo() {
        exector.execute{dataSource.deletInfo()}
    }
}