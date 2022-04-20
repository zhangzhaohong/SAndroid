package com.tristana.sandroid.downloader.utils

import android.content.Context
import android.net.Uri
import com.blankj.utilcode.util.FileUtils
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/18 16:37
 * @description
 */
object RequestObjectUtils {
    fun getFetchRequests(
        context: Context,
        url: String,
        filePath: String?,
        groupId: Int,
        priority: Priority = Priority.NORMAL
    ): List<Request> {
        val requests: MutableList<Request> = ArrayList()
        val request = Request(url, filePath ?: kotlin.run { getFilePath(context, url) })
        request.priority = priority
        request.groupId = groupId
        requests.add(request)
        return requests
    }

    private fun getFilePath(context: Context, url: String): String {
        var filePath = context.getExternalFilesDir("download")?.absolutePath
        FileUtils.createOrExistsDir(filePath)
        val uri = Uri.parse(url)
        val fileName = uri.lastPathSegment
        filePath = "$filePath/$fileName"
        return filePath
    }
}