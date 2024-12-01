package com.example.hkubuddy

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

//This file is used for parsing the .ics calendar file and store it into the database.
object ICSParser {

    fun parseICSFile(uri: Uri?, context: Context) {
        // Initialize database helper and get writable database
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase

        try {
            if (uri != null) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    // Use BufferedReader to read the ICS file line by line
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String?
                        var name: String? = null
                        var description: String? = null
                        var deadline: String? = null

                        // Read each line of the file
                        while (reader.readLine().also { line = it } != null) {
                            val trimmedLine = line?.trim() ?: continue

                            // Parse different components of the ICS file
                            when {
                                // Extract event title
                                trimmedLine.startsWith("SUMMARY:") -> {
                                    name = trimmedLine.removePrefix("SUMMARY:").trim()
                                }
                                // Extract event description
                                trimmedLine.startsWith("DESCRIPTION:") -> {
                                    description = trimmedLine.removePrefix("DESCRIPTION:").trim()
                                }
                                // Extract event start date and format it
                                trimmedLine.startsWith("DTSTART:") -> {
                                    deadline =
                                        formatDate(trimmedLine.removePrefix("DTSTART:").trim())
                                }
                                // When reaching the end of an event, insert it into the database
                                trimmedLine.startsWith("END:VEVENT") -> {
                                    if (!name.isNullOrEmpty() && !deadline.isNullOrEmpty()) {
                                        // Create a Task object
                                        val task = Task(
                                            name = name,
                                            description = description ?: "",
                                            deadline = deadline,
                                            isComplete = false
                                        )

                                        // Insert the task into the database
                                        dbHelper.addTask(task)
                                        // Reset variables for the next event
                                        name = null
                                        description = null
                                        deadline = null
                                        Log.e("ICSParser", "Task added: $task")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Log.e("ICSParser", "Error reading .ics file")
            }
        } catch (e: Exception) {
            // Log any errors that occur during file reading or parsing
            Log.e("ICSParser", "Error reading .ics file", e)
            throw e
        } finally {
            // Ensure the database is closed after operations are complete
            db.close()
        }
    }


    private fun formatDate(date: String): String {
        return if (date.length >= 8) {
            // Extract year, month, and day from the input string
            "${date.substring(0, 4)}/${date.substring(4, 6)}/${date.substring(6, 8)}"
        } else date // Return the original string if it's shorter than 8 characters
    }
}
