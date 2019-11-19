package com.kinvey.sample.statusshare.utils

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import timber.log.Timber
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object FileUtil {

    const val TMP_AVATAR_NAME = "tmp_avatar_"
    const val JPG_EXT = ".jpg"

    @JvmStatic
    fun getCaptureFilePath(): Uri {
        val fileName = "$TMP_AVATAR_NAME${System.currentTimeMillis()}$JPG_EXT"
        val file = File(Environment.getExternalStorageDirectory(),fileName)
        return Uri.fromFile(file)
    }

    /**
     * This method gets a file location from a URI
     *
     * @param contentUri the URI of the image to upload
     * @return the file path of the image
     */
    @JvmStatic
    fun getRealPathFromURI(context: Activity, contentUri: Uri?): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME)
        val cursor: Cursor = context.managedQuery(contentUri, proj, null, null, null) ?: return ""
        val ret = ""
        var columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        if (columnIndex == -1) {
            //picassa bug
            //see: http://jimmi1977.blogspot.com/2012/01/android-api-quirks-getting-image-from.html
            columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
        }
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    fun getAvatarUrl(gravatarID: String?): String {
        var url = ""
        try {
            val digester = MessageDigest.getInstance("MD5")
            val digest = digester.digest(gravatarID?.toByteArray())
            val convStr = digest.joinToString { b ->
                val conv = (b.toInt() and 0xff) + 0x100
                conv.toString(16).substring(1)
            }
            url = "http://www.gravatar.com/avatar/$convStr.jpg?d=identicon"
            //Timber.d(gravatarID + " = " + url);
        } catch (e: NoSuchAlgorithmException) {
            Timber.e(e)
        }
        return url
    }
}