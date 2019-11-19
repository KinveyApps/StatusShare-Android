package com.kinvey.sample.statusshare.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.net.Uri
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException

object BitmapTools {

    private const val THUMBNAIL_HEIGHT = 512 // height in pixels
    private const val THUMBNAIL_WIDTH  = 512 // width in pixels

    @JvmStatic
    fun decodeFromFile(activity: Activity, pathUri: Uri?): Bitmap? {
        val path = FileUtil.getFilePath(activity, pathUri)
        return BitmapFactory.decodeFile(path)
    }

    @JvmStatic
    fun decodeFromCameraUri(activity: Activity, pathUri: Uri?): Bitmap? {
        val path = FileUtil.getFilePath(activity, pathUri)
        Timber.i("activity result, path from camera is -> ${path == null}")
        val options = Options()
        options.inScaled = true
        options.inJustDecodeBounds = true
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.decodeFile(path, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 200, 150)
        options.inJustDecodeBounds = false
        // Decode bitmap with inSampleSize set
        // Decode bitmap with inSampleSize set
        return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path, options),
                THUMBNAIL_HEIGHT, THUMBNAIL_WIDTH, false)
    }

    @JvmStatic
    fun compressImage(image: Bitmap?): ByteArray {
        var byteArray: ByteArray? = null
        try {
            val stream = ByteArrayOutputStream()
            image?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            byteArray = stream.toByteArray()
            stream.close()
        } catch (e: IOException) {
            Timber.e(e)
        }
        return byteArray ?: byteArrayOf()
    }

    /**
     * used to scale images
     *
     * @param options   scaling options
     * @param reqWidth  the target width
     * @param reqHeight the target height
     * @return width/height of the sample image, as close as possible to input req*
     */
    private fun calculateInSampleSize(options: Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        val stretchWidth = Math.round(width.toFloat() / reqWidth.toFloat())
        val stretchHeight = Math.round(height.toFloat() / reqHeight.toFloat())
        return if (stretchWidth <= stretchHeight) stretchHeight else stretchWidth
    }
}