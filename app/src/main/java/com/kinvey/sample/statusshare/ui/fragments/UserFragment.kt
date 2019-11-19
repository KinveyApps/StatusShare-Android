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

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.kinvey.android.callback.KinveyReadCallback
import com.kinvey.android.store.DataStore
import com.kinvey.java.Query
import com.kinvey.java.model.KinveyReadResponse
import com.kinvey.java.query.AbstractQuery.SortOrder
import com.kinvey.java.store.StoreType
import com.kinvey.sample.statusshare.R
import com.kinvey.sample.statusshare.model.UpdateEntity
import com.kinvey.sample.statusshare.ui.component.UpdateAdapter
import com.kinvey.sample.statusshare.utils.Constants
import com.kinvey.sample.statusshare.utils.FileUtil.getAvatarUrl
import kotlinx.android.synthetic.main.fragment_view_author.*
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * @author edwardf
 * @since 2.0
 */
class UserFragment private constructor() : KinveyFragment() {

    private var dataStore: DataStore<UpdateEntity>? = null
    private var updates: MutableList<UpdateEntity>? = null
    private var source: UpdateEntity? = null
    private var gravatar: Bitmap? = null

    override val viewID = R.layout.fragment_view_author

    override fun bindViews(v: View) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dataStore = DataStore.collection(Constants.COL_UPDATES, UpdateEntity::class.java, StoreType.AUTO, client)
    }

    override fun populateViews() {
        if (source == null) { return }
        authorNameText?.text = source?.authorName
        setAvatar(source?.authorName)
        if (authorUpdateList?.adapter == null || authorUpdateList?.adapter?.count == 0) {
            updateList()
        }
    }

    private fun updateList() {
        authorListProgress?.visibility = View.VISIBLE
        authorUpdateList?.visibility = View.GONE
        val q = Query()
        q.equals("_acl.creator", source?.authorID)
        q.addSort("_kmd.lmt", SortOrder.DESC)
        q.setLimit(UPDATES_LIST_SIZE)
        dataStore?.find(q, object : KinveyReadCallback<UpdateEntity> {
            override fun onSuccess(result: KinveyReadResponse<UpdateEntity>?) {
                val list = result?.result ?: listOf()
                Timber.d("Count of updates found: ${list?.size}")
                if (activity == null) { return }
                for (entity in list) {
                    Timber.d("result -> $entity")
                    Timber.d("attachment? -> ${entity.getFile(Constants.ATTACHMENT_NAME) == null}")
                    if (entity.getFile(Constants.ATTACHMENT_NAME) != null) {
                        Timber.d("outputstream ? -> ${entity.getFile(Constants.ATTACHMENT_NAME)?.output == null}")
                    }
                }
                updates = ArrayList(list)
                authorUpdateList?.adapter = UpdateAdapter(activity!!, updates as List<UpdateEntity>, activity!!.layoutInflater)
                authorListProgress?.visibility = View.GONE
                authorUpdateList?.visibility = View.VISIBLE
            }
            override fun onFailure(error: Throwable) {
                Timber.w("Error fetching updates data: ${error.message}")
            }
        }, null)
        // new String[]{"author"}, 1, true
    }

    fun setSource(ent: UpdateEntity) {
        source = ent
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    private fun setAvatar(gravatarID: String?) {
        val url = getAvatarUrl(gravatarID)
        DownloadAvatarTask().execute(url)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DownloadAvatarTask : AsyncTask<String?, Void?, Bitmap?>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            try {
                gravatar = BitmapFactory.decodeStream(URL(params[0]).content as InputStream)
            } catch (e: MalformedURLException) {
                Timber.e("url for avatar download is bad")
            } catch (e: IOException) {
                Timber.e("failed to download avatar")
            }
            return gravatar
        }
        override fun onPostExecute(grav: Bitmap?) {
            if (gravatar != null) {
                avatarImage?.setImageBitmap(gravatar)
            }
        }
    }

    companion object {
        const val UPDATES_LIST_SIZE = 5
        fun newInstance(update: UpdateEntity): UserFragment {
            val ret = UserFragment()
            ret.setSource(update)
            ret.setHasOptionsMenu(true)
            return ret
        }
    }
}