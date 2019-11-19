package com.kinvey.sample.statusshare.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.api.client.http.HttpTransport
import com.kinvey.sample.statusshare.R.id
import com.kinvey.sample.statusshare.R.layout
import com.kinvey.sample.statusshare.model.UpdateEntity
import com.kinvey.sample.statusshare.ui.fragments.LoginFragment
import com.kinvey.sample.statusshare.utils.BitmapTools.decodeFromCameraUri
import com.kinvey.sample.statusshare.utils.BitmapTools.decodeFromFile
import com.kinvey.sample.statusshare.utils.Constants
import com.kinvey.sample.statusshare.utils.FileUtil.getCaptureFilePath
import timber.log.Timber
import java.util.logging.Level
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    private var mImageCaptureUri: Uri? = null
    var bitmap: Bitmap? = null
    var shareList: List<UpdateEntity>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_fragment_holder)
        Logger.getLogger(HttpTransport::class.java.name).level = LOGGING_LEVEL
        replaceFragment(LoginFragment(), false)
    }

    fun replaceFragment(frag: Fragment, addToBackStack: Boolean) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(id.fragmentBox, frag)
        if (addToBackStack) {
            ft.addToBackStack(frag.toString())
        }
        ft.commit()
    }

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

    fun startCamera() {
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
    fun startFilePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), Constants.PICK_FROM_FILE)
    }

    companion object {
        private val LOGGING_LEVEL: Level? = Level.FINEST
    }
}