/**
 * Copyright (c) 2019 Kinvey Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.kinvey.sample.statusshare.ui.fragments

import android.R.layout
import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.ArrayAdapter
import com.kinvey.android.store.LinkedDataStore
import com.kinvey.java.AbstractClient
import com.kinvey.java.core.KinveyClientCallback
import com.kinvey.java.linkedResources.LinkedFile
import com.kinvey.java.model.KinveyMetaData.AccessControlList
import com.kinvey.java.store.StoreType
import com.kinvey.sample.statusshare.R
import com.kinvey.sample.statusshare.model.UpdateEntity
import com.kinvey.sample.statusshare.utils.BitmapTools
import com.kinvey.sample.statusshare.utils.Constants
import com.kinvey.sample.statusshare.utils.Constants.ACL_FIELD
import com.kinvey.sample.statusshare.utils.Constants.ATTACHMENT_FIELD
import com.kinvey.sample.statusshare.utils.Constants.PUBLIC_FIELD
import com.kinvey.sample.statusshare.utils.Constants.USERNAME_FIELD_NAME
import com.kinvey.sample.statusshare.utils.UiUtils
import kotlinx.android.synthetic.main.fragment_write_update.*
import timber.log.Timber
import java.io.ByteArrayInputStream

/**
 * @author edwardf
 * @since 2.0
 */
class UpdateEditFragment : KinveyFragment(), OnClickListener {

    private var mDialog: AlertDialog? = null
    private var image: Bitmap? = null
    private var dataStore: LinkedDataStore<UpdateEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = LinkedDataStore(client as AbstractClient<*>, Constants.COL_UPDATES, UpdateEntity::class.java, StoreType.AUTO)
        setHasOptionsMenu(true)
        val items = arrayOf("From Camera", "From SD Card")
        val adapter = ArrayAdapter(activity!!, layout.select_dialog_item, items)
        val builder = Builder(activity)
        builder.setTitle("Select Image")
        builder.setAdapter(adapter) { dialog, item ->
            if (item == 0) {
                mainActivity?.startCamera()
                dialog.cancel()
            } else {
                mainActivity?.startFilePicker()
                dialog.cancel()
            }
        }
        mDialog = builder.create()
    }

    override fun onResume() {
        super.onResume()
        image = mainActivity?.bitmap
        if (image != null) {
            Timber.i("setting imageview")
            previewImage?.setBackgroundResource(0)
            previewImage?.setImageBitmap(image)
        } else {
            Timber.i("not setting imageview")
        }
    }

    override val viewID = R.layout.fragment_write_update

    override fun bindViews(v: View) {
        previewImage?.setOnClickListener(this)
    }

    private fun doUpdate() {
        val progressDialog = ProgressDialog.show(activity, "", "Posting. Please wait...", true)
        val byteArray = BitmapTools.compressImage(image)
        val fileName = attachmentFilename()
        saveUpdateAttachment(progressDialog, byteArray, fileName)
    }

    private fun attachmentFilename(): String? {
        return "${client?.activeUser?.id}_attachment_${System.currentTimeMillis()}.png"
    }

    private fun saveUpdateAttachment(progressDialog: ProgressDialog, bytes: ByteArray?, filename: String?) {
        val updateEntity = UpdateEntity(userId = client?.activeUser?.id)
        updateEntity.authorName = (client?.activeUser)?.get(USERNAME_FIELD_NAME)?.toString()
        updateEntity.text = updateEdit.text.toString()
        updateEntity.acl?.setGloballyReadable(true)
        Timber.d("updateEntity.getMeta().isGloballyReadable() = ${updateEntity.acl?.isGloballyReadable}")
        if (bytes != null && filename != null) {
            Timber.i("there is an attachment!")
            val lf = LinkedFile(filename)
            lf.addExtra(PUBLIC_FIELD, true)
            val acl = AccessControlList()
            acl.setGloballyReadable(true)
            lf.addExtra(ACL_FIELD, acl)
            updateEntity.putFile(ATTACHMENT_FIELD, lf)
        }
        val bais = if (bytes == null) null else ByteArrayInputStream(bytes)
        if (bais != null) {
            updateEntity.getFile(ATTACHMENT_FIELD)?.input = bais
        }
        dataStore?.save(updateEntity, object : KinveyClientCallback<UpdateEntity> {
            override fun onSuccess(result: UpdateEntity) {
                if (activity == null) { return }
                Timber.d("postUpdate: SUCCESS _id = ${result.id} gr = ${result.acl?.isGloballyReadable}")
                progressDialog.dismiss()
                try {
                    bais?.close()
                } catch (e: Exception) {
                    Timber.e(e)
                }
                UiUtils.hideKeyboard(activity)
                mainActivity?.bitmap = null
                if (activity != null) {
                    mainActivity?.shareList = null
                    replaceFragment(ShareListFragment(), false)
                }
            }
            override fun onFailure(error: Throwable) {
                if (activity == null) { return }
                Timber.d("failed to upload linked app data")
                progressDialog.dismiss()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_edit_share, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_send_post -> doUpdate()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        if (v === previewImage) { mDialog?.show() }
    }
}