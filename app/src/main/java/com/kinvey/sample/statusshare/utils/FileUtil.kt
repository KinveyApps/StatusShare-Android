package com.kinvey.sample.statusshare.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.provider.MediaStore.Images
import android.provider.MediaStore.Video
import com.kinvey.sample.statusshare.BuildConfig
import timber.log.Timber
import java.io.File
import java.lang.Long
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object FileUtil {

   private const val TMP_AVATAR_NAME = "tmp_avatar_"
   private const val JPG_EXT = ".jpg"

   private const val DOWNLOADS_CONTENT_PATH = "content://downloads/public_downloads"

   private const val PRIMARY_TYPE = "primary"

   private const val IMAGE_TYPE = "image"
   private const val VIDEO_TYPE = "video"
   private const val AUDIO_TYPE = "audio"

   private const val CONTENT_SCHEME = "content"
   private const val FILE_SCHEME = "file"

   private const val SELECTION_QUERY = "_id=?"

    @JvmStatic
    fun getCaptureFilePath(): Uri {
        val fileName = "$TMP_AVATAR_NAME${System.currentTimeMillis()}$JPG_EXT"
        val file = File(Environment.getExternalStorageDirectory(),fileName)
        return Uri.fromFile(file)
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     * @author paulburke
     */
    @JvmStatic
    fun getFilePath(context: Context?, pathUri: Uri?): String? {
        val uri = pathUri ?: return null
        if (BuildConfig.DEBUG) {
            Timber.d("Authority: ${uri.authority},\nFragment: ${uri.fragment},\nPort: ${uri.port},\n")
            Timber.d("Query: ${uri.query},\nScheme: ${uri.scheme},\nHost: ${uri.host},\nSegments: ${uri.pathSegments}")
        }
        val isKitKat = VERSION.SDK_INT >= VERSION_CODES.KITKAT
        if (isKitKat && isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = getDocumentId(uri)
                val split = docId?.split(":")?.toTypedArray() ?: arrayOf()
                val type = split[0]
                if (PRIMARY_TYPE.equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse(DOWNLOADS_CONTENT_PATH), id?.toLong() ?: 0)
                return getDataColumn(context, contentUri, null, null)
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                val docId = getDocumentId(uri)
                val split = docId?.split(":")?.toTypedArray() ?: arrayOf()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    IMAGE_TYPE -> contentUri = Images.Media.EXTERNAL_CONTENT_URI
                    VIDEO_TYPE -> contentUri = Video.Media.EXTERNAL_CONTENT_URI
                    AUDIO_TYPE -> contentUri = Media.EXTERNAL_CONTENT_URI
                }
                if (contentUri != null) {
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, SELECTION_QUERY, selectionArgs)
                }
            }
        }
        // MediaStore (and general)
        else if (CONTENT_SCHEME.equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)
        }
        // File
        else if (FILE_SCHEME.equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Drive.
     */
    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    private fun getDataColumn(context: Context?, uri: Uri, selection: String?,
                      selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context?.contentResolver?.query(uri, projection,
                    selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                if (BuildConfig.DEBUG) DatabaseUtils.dumpCursor(cursor)
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    @TargetApi(VERSION_CODES.KITKAT)
    private fun isDocumentUri(context: Context?, uri: Uri?): Boolean {
        return DocumentsContract.isDocumentUri(context, uri)
    }

    @TargetApi(VERSION_CODES.KITKAT)
    private fun getDocumentId(uri: Uri?): String? {
        return DocumentsContract.getDocumentId(uri)
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