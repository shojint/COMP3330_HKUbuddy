package com.example.hkubuddy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log

class ProfileDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ProfileDB"
        private const val PROFILE_TABLE = "Profile"
        private const val KEY_USERNAME = "User"
        private const val KEY_IMAGE = "Image"
//        private const val KEY_IMAGE = "Image BLOB"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + PROFILE_TABLE + " (" +
                KEY_USERNAME + " TEXT," +
                KEY_IMAGE + " TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $PROFILE_TABLE")
        onCreate(db)
    }


    @Throws(SQLiteException::class)
    fun updateEntry(context: Context, name: String?, image: Uri?) {
        Log.d("Name", "Name: " + name + ", Image: " + image.toString());

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_USERNAME, name)

//        // Put Image URI to cv
//        cv.put(KEY_IMAGE, image?.toString())

        // Convert image URI to byte array
        image?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val byteArray = inputStream?.readBytes()
            cv.put(KEY_IMAGE, byteArray)
        }

        for (key in cv.keySet()) {
            val value = cv.get(key)
            Log.d("Name", "$key: $value")
        }

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

    fun getUserImage(context: Context): Uri? {
        val db = this.readableDatabase
        val query = "SELECT $KEY_IMAGE FROM $PROFILE_TABLE"
        val cursor = db.rawQuery(query, null)
        var imageUri: Uri? = null
        cursor.use {
            if (it.moveToFirst()) {
//                val uriString = it.getString(0)
//                imageUri = if (!uriString.isNullOrEmpty()) Uri.parse(uriString) else null

//
                val blob = it.getBlob(0)

                if (blob != null) {
//                    if (it.moveToFirst()){
//                        val uriString = it.getString(0)
//                        imageUri = if (!uriString.isNullOrEmpty()) Uri.parse(uriString) else null
//                    }

                    // Convert byte array back to Bitmap and then to URI
                    val bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                    val path = MediaStore.Images.Media.insertImage(
                        context.contentResolver,
                        bitmap,
                        "Profile Image",
                        null
                    )
                    if (path != null) {
                        imageUri = Uri.parse(path)
                    }

                    Log.d("Image", "imageUri: " + imageUri.toString())

                } else {
                    // Handle the case where the BLOB is null
                    Log.e("ProfileDatabase", "No image found in the database.")
                }
            }
        }
        db.close()
        return imageUri
    }
}