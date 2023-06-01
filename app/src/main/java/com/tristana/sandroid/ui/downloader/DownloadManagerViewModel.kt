package com.tristana.sandroid.ui.downloader

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Fetch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadManagerViewModel : ViewModel() {

    private var pageSize = MutableLiveData(10)
    var hasMore = MutableLiveData(true)
    var taskListData = MutableLiveData<MutableList<Download>>(ArrayList())
    var fileInfoList = MutableLiveData<MutableList<Download>>(ArrayList())

    suspend fun getData(fetch: Fetch?, groupId: Int) {
        return withContext(Dispatchers.IO) {
            kotlin.run {
                fetch?.getDownloadsInGroup(groupId) { taskList ->
                    val data: ArrayList<Download> = ArrayList()
                    data.addAll(taskList)
                    sortData(data)?.let { currentTaskListData ->
                        taskListData.value = currentTaskListData
                        val pageData: ArrayList<Download> = ArrayList()
                        val indicate = if (currentTaskListData.size > pageSize.value!! - 1) {
                            pageSize.value!! - 1
                        } else {
                            currentTaskListData.size - 1
                        }
                        for (index in 0..indicate) {
                            pageData.add(currentTaskListData[index])
                        }
                        fileInfoList.value = pageData
                        hasMore.value = data.size != pageData.size
                    }
                }
            }
        }
    }

    fun addOrUpdate(download: Download, isAdd: Boolean = false) {
        val data: ArrayList<Download> = fileInfoList.value as ArrayList<Download>
        if (isAdd) {
            data.add(0, download)
        } else {
            fileInfoList.value?.indexOfFirst { item -> item.id == download.id }?.let { index ->
                if (index >= 0 && index < data.size) {
                    data[index] = download
                }
            }
        }
        fileInfoList.value = sortData(data)
    }

    fun loadMore() {
        val pageData: ArrayList<Download> = fileInfoList.value as ArrayList<Download>
        val taskListData: ArrayList<Download> = taskListData.value as ArrayList<Download>
        if (pageData.isEmpty()) {
            return
        }
        pageData[pageData.size - 1].let { lastItem ->
            taskListData.indexOfFirst { item -> item.id == lastItem.id }.let {
                val indicate = if (taskListData.size - 1 >= it + pageSize.value!!) {
                    it + pageSize.value!!
                } else {
                    taskListData.size - 1
                }
                for (index in it + 1..indicate) {
                    pageData.add(taskListData[index])
                }
                hasMore.value = indicate < taskListData.size - 1
            }
        }
        fileInfoList.value = sortData(pageData)
    }

    private fun sortData(input: ArrayList<Download>?): ArrayList<Download>? {
        input?.sortByDescending {
            it.created
        }
        return input
    }

    companion object {
        const val TAG = "DownloadManagerViewModel"
    }
}