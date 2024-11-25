package com.example.hkubuddy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class ProfileDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ProfileDB"
        private const val PROFILE_TABLE = "Profile"
        private const val KEY_USERNAME = "User"
        private const val KEY_IMAGE = "Image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + PROFILE_TABLE + " ("+
                KEY_USERNAME + " TEXT," +
                KEY_IMAGE + " TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $PROFILE_TABLE")
        onCreate(db)
    }

    @Throws(SQLiteException::class)
    fun updateEntry(name: String?, image: Uri?) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_USERNAME, name)
        cv.put(KEY_IMAGE, image?.toString())

        // Try to update existing record
        val rowsAffected = db.update(PROFILE_TABLE, cv, null, null)

        // If no rows were updated, insert a new record
        if (rowsAffected == 0) {
            db.insert(PROFILE_TABLE, null, cv)
        }

        db.close()
    }

    fun getItemCount(): Int {
        val db = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $PROFILE_TABLE"
        val cursor = db.rawQuery(countQuery, null)
        var count = 0
        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        db.close()
        return count
    }

    fun getUsername(): String {
        val db = this.readableDatabase
        val query = "SELECT $KEY_USERNAME FROM $PROFILE_TABLE"
        val cursor = db.rawQuery(query, null)
        var username = ""
        cursor.use {
            if (it.moveToFirst()) {
                username = it.getString(0)
            }
        }
        db.close()
        return username
    }

    fun getUserImage(): Uri? {
        val db = this.readableDatabase
        val query = "SELECT $KEY_IMAGE FROM $PROFILE_TABLE"
        val cursor = db.rawQuery(query, null)
        var image: Uri? = null
        cursor.use {
            if (it.moveToFirst()){
                val uriString = it.getString(0)
                image = if (uriString.isNullOrEmpty()) Uri.parse(uriString) else null
            }
        }
        db.close()
        return image
    }
}