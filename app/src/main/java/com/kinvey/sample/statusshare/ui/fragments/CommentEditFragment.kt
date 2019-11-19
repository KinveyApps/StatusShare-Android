/**
 * Copyright (c) 2014 Kinvey Inc.
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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.kinvey.android.store.DataStore
import com.kinvey.java.core.KinveyClientCallback
import com.kinvey.java.model.KinveyReference
import com.kinvey.java.store.StoreType
import com.kinvey.sample.statusshare.utils.Constants
import com.kinvey.sample.statusshare.ui.MainActivity
import com.kinvey.sample.statusshare.R
import com.kinvey.sample.statusshare.utils.UiUtils
import com.kinvey.sample.statusshare.model.CommentEntity
import com.kinvey.sample.statusshare.model.UpdateEntity
import kotlinx.android.synthetic.main.fragment_edit_comment.*
import timber.log.Timber

/**
 * @author edwardf
 * @since 2.0
 */
class CommentEditFragment : KinveyFragment() {

    private var parent: UpdateEntity? = null
    private var dataStoreUpdate: DataStore<UpdateEntity>? = null
    private var dataStoreComment: DataStore<CommentEntity>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreUpdate = DataStore.collection(Constants.COL_UPDATES, UpdateEntity::class.java, StoreType.AUTO, client)
        dataStoreComment = DataStore.collection(Constants.COL_COMMENTS, CommentEntity::class.java, StoreType.AUTO, client)
    }

    override val viewID = R.layout.fragment_edit_comment

    override fun bindViews(v: View) {
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_edit_share, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_send_post -> saveComment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveComment() {
        UiUtils.hideKeyboard(activity)
        val ent = CommentEntity(commentTitle?.text.toString())
        ent.acl?.setGloballyReadable(true)
        ent.author = client?.activeUser?.username
        val updateAuthor = KinveyReference(parent?.author?.collection, parent?.author?.id)
        parent?.author = updateAuthor
        parent?.resetCommentReferences()
        dataStoreComment?.save(ent, object : KinveyClientCallback<CommentEntity> {
            override fun onSuccess(commentEntity: CommentEntity) {
                if (activity == null) { return }
                parent?.let { entity ->
                    entity.addComment(commentEntity)
                    saveUpdateEntity(entity)
                }
            }
            override fun onFailure(throwable: Throwable) {
                Timber.e("error adding comment -> $throwable")
            }
        })
    }

    private fun saveUpdateEntity(entity: UpdateEntity) {
        dataStoreUpdate?.save(entity, object : KinveyClientCallback<UpdateEntity> {
            override fun onSuccess(updateEntity: UpdateEntity) {
                if (activity == null) { return }
                if (activity != null) {
                    (activity as MainActivity?)?.shareList = null
                    (activity as MainActivity?)?.replaceFragment(ShareListFragment(), false)
                }
            }
            override fun onFailure(throwable: Throwable) {
                Timber.e("error adding update entity -> $throwable")
            }
        })
    }

    fun getParent(): UpdateEntity? {
        return parent
    }

    fun setParent(parent: UpdateEntity?) {
        this.parent = parent
    }

    companion object {
        fun newInstance(parent: UpdateEntity?): CommentEditFragment {
            val ret = CommentEditFragment()
            ret.setHasOptionsMenu(true)
            ret.setParent(parent)
            return ret
        }
    }
}