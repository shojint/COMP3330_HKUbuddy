package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.FileReader

object ICSParser {

    fun parseICSFile(filePath: String, context: Context) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase

        try {
            BufferedReader(FileReader(filePath)).use { reader ->
                var line: String?
                var title: String? = null
                var description: String? = null
                var startDate: String? = null

                while (reader.readLine().also { line = it } != null) {
                    val trimmedLine = line?.trim() ?: continue

                    when {
                        trimmedLine.startsWith("SUMMARY:") -> {
                            title = trimmedLine.removePrefix("SUMMARY:").trim()
                        }
                        trimmedLine.startsWith("DESCRIPTION:") -> {
                            description = trimmedLine.removePrefix("DESCRIPTION:").trim()
                        }
                        trimmedLine.startsWith("DTSTART:") -> {
                            startDate = formatDate(trimmedLine.removePrefix("DTSTART:").trim())
                        }
                        trimmedLine.startsWith("END:VEVENT") -> {
                            if (!title.isNullOrEmpty() && !startDate.isNullOrEmpty()) {
                                val values = ContentValues().apply {
                                    put("title", title)
                                    put("description", description ?: "")
                                    put("date", startDate)
                                }
                                db.insert("events", null, values)

                                title = null
                                description = null
                                startDate = null
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ICSParser", "Error reading .ics file", e)
        } finally {
            db.close()
        }
    }

    private fun formatDate(date: String): String {
        return if (date.length >= 8) {
            "${date.substring(0, 4)}-${date.substring(4, 6)}-${date.substring(6, 8)}"
        } else date
    }
}
