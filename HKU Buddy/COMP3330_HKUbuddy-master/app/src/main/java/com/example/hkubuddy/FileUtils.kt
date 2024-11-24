package com.example.hkubuddy

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

object FileUtils {
    fun getPath(context: Context, uri: Uri?): String? {
        if (uri == null) return null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                val name = it.getString(nameIndex)
                return "${context.filesDir}/$name"
            }
        }
        return null
    }
}
