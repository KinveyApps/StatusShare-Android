package com.kinvey.sample.statusshare.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.google.api.client.http.HttpTransport
import com.kinvey.sample.statusshare.R
import com.kinvey.sample.statusshare.model.UpdateEntity
import com.kinvey.sample.statusshare.ui.fragments.LoginFragment
import com.kinvey.sample.statusshare.utils.BitmapTools.decodeFromCameraUri
import com.kinvey.sample.statusshare.utils.BitmapTools.decodeFromFile
import com.kinvey.sample.statusshare.utils.Constants
import com.kinvey.sample.statusshare.utils.FileUtil.getCaptureFilePath
import kotlinx.android.synthetic.main.activity_fragment_holder.*
import timber.log.Timber
import java.util.logging.Level
import java.util.logging.Logger

open class MainActivity : BaseCompatActivity() {

    private var mImageCaptureUri: Uri? = null
    var bitmap: Bitmap? = null
    var shareList: List<UpdateEntity>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(myToolbar)
        Logger.getLogger(HttpTransport::class.java.name).level = LOGGING_LEVEL
        replaceFragment(LoginFragment(), false)
    }

    override val layoutId = R.layout.activity_fragment_holder

    override val contentId = R.id.fragmentBox

    /**
     * This method will be called after the user either selects a photo from their gallery or takes a picture with their camera
     *
     * @param requestCode - either PICK_FROM_FILE or PICK_FROM_CAMERA
     * @param resultCode  - hopefully it's RESULT_OK!
     * @param data        - contains the Path to the selected image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) { return }
        if (requestCode == Constants.PICK_FROM_FILE) {
            mImageCaptureUri = data?.data
            bitmap = decodeFromFile(this, mImageCaptureUri)
            Timber.i("activity result, bitmap from file is -> ${bitmap == null}")
        } else if (requestCode == Constants.PICK_FROM_CAMERA) {
            bitmap = decodeFromCameraUri(this, mImageCaptureUri)
        } else {
            Timber.e("That's not a valid request code! -> $requestCode")
        }
    }

    fun startCamera(requestPermissions: Boolean = true) {
        if (!verifyCameraPermissions(this)) {
            if (requestPermissions) { requestCameraPermissions(this) }
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mImageCaptureUri = getCaptureFilePath()
        try {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
            intent.putExtra("return-data", true)
            startActivityForResult(intent, Constants.PICK_FROM_CAMERA)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * This method wraps the code to kick off the "chooser" intent, which allows user to select where to select image from
     */
    fun startFilePicker(requestPermissions: Boolean = true) {
        if (!verifyStoragePermissions(this)) {
            if (requestPermissions) { requestStoragePermissions(this) }
            return
        }
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), Constants.PICK_FROM_FILE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> startFilePicker(false)
            REQUEST_CAMERA -> startCamera(false)
        }
    }

    companion object {
        private val LOGGING_LEVEL: Level? = Level.FINEST

        private const val REQUEST_EXTERNAL_STORAGE = 1
        private const val REQUEST_CAMERA = 2

        private val PERMISSIONS_STORAGE = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        private val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)

        /**
         * Checks if the app has permission to write to device storage
         *
         * If the app does not has permission then the user will be prompted to grant permissions
         *
         * @param activity
         */
        fun verifyStoragePermissions(activity: Activity): Boolean {
            // Check if we have write permission
            val permissionWrite = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permissionRead = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            return permissionWrite == PackageManager.PERMISSION_GRANTED && permissionRead == PackageManager.PERMISSION_GRANTED
        }

        fun requestStoragePermissions(activity: Activity) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
        }

        fun verifyCameraPermissions(activity: Activity): Boolean {
            // Check if we have permission
            val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            return permission == PackageManager.PERMISSION_GRANTED
        }

        fun requestCameraPermissions(activity: Activity) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CAMERA, REQUEST_CAMERA)
        }
    }
}