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

import android.os.Bundle
import android.view.*
import com.kinvey.android.callback.KinveyReadCallback
import com.kinvey.android.store.LinkedDataStore
import com.kinvey.java.AbstractClient
import com.kinvey.java.Query
import com.kinvey.java.model.KinveyReadResponse
import com.kinvey.java.query.AbstractQuery.SortOrder
import com.kinvey.java.store.StoreType
import com.kinvey.sample.statusshare.R
import com.kinvey.sample.statusshare.model.CommentEntity
import com.kinvey.sample.statusshare.model.UpdateEntity
import com.kinvey.sample.statusshare.ui.adapter.CommentAdapter
import com.kinvey.sample.statusshare.utils.Constants
import com.kinvey.sample.statusshare.utils.Constants.KMD_LMT_FIELD_NAME
import com.kinvey.sample.statusshare.utils.Constants.UPDATE_ID_NAME
import kotlinx.android.synthetic.main.fragment_update_details.*
import timber.log.Timber
import kotlin.collections.ArrayList

/**
 * @author edwardf
 * @since 2.0
 */
class UpdateDetailsFragment : KinveyFragment() {
    
    private var entity: UpdateEntity? = null
    private var dataStore: LinkedDataStore<CommentEntity>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = LinkedDataStore(client as AbstractClient<*>, Constants.COL_COMMENTS, CommentEntity::class.java, StoreType.AUTO)
    }

    override val viewID = R.layout.fragment_update_details

    override fun bindViews(v: View) {
        activity?.actionBar?.title = "View Update"
    }

    override fun populateViews() {
        if (entity?.thumbnail != null) {
            updateImage.setImageBitmap(entity?.thumbnail)
        } else {
            updateImage.visibility = View.GONE
        }
        updateText?.text = entity?.text
        updateAuthorText?.text = entity?.authorName
        val q = Query()
            .equals(UPDATE_ID_NAME, entity?.id)
            .addSort(KMD_LMT_FIELD_NAME, SortOrder.ASC)
        dataStore?.find(q, object : KinveyReadCallback<CommentEntity> {
            override fun onSuccess(result: KinveyReadResponse<CommentEntity>?) {
                if (result == null) { return }
                val list = result.result ?: listOf()
                Timber.d("Count of comments found: ${list.size}")
                for (e in list) {
                    Timber.d("comment -> $e")
                }
                if (activity == null) { return }
                val comments = ArrayList(list)
                val adapter = CommentAdapter(activity, comments, activity?.layoutInflater as LayoutInflater)
                updateCommentList?.adapter = adapter
            }
            override fun onFailure(error: Throwable) {
                Timber.w("Error fetching comments data: ${error.message}")
            }
        }, null)
        //, new String[]{"text", "author", "updateId"}, 3, true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_view_update, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_comment -> if (entity != null) {
                addComment()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addComment() {
        replaceFragment(CommentEditFragment.newInstance(entity), false)
    }

    companion object {
        fun newInstance(entity: UpdateEntity?): UpdateDetailsFragment {
            val frag = UpdateDetailsFragment()
            frag.entity = entity
            frag.setHasOptionsMenu(true)
            return frag
        }
    }
}