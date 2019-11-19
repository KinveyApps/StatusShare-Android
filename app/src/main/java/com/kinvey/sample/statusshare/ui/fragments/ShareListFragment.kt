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

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener

import com.kinvey.android.callback.KinveyReadCallback
import com.kinvey.android.store.DataStore
import com.kinvey.android.store.LinkedDataStore
import com.kinvey.android.store.UserStore
import com.kinvey.java.AbstractClient
import com.kinvey.java.Query
import com.kinvey.java.core.KinveyClientCallback
import com.kinvey.java.dto.BaseUser
import com.kinvey.java.model.KinveyReadResponse
import com.kinvey.java.query.AbstractQuery.SortOrder
import com.kinvey.java.store.StoreType
import com.kinvey.sample.statusshare.R
import com.kinvey.sample.statusshare.model.CommentEntity
import com.kinvey.sample.statusshare.model.UpdateEntity
import com.kinvey.sample.statusshare.ui.MainActivity
import com.kinvey.sample.statusshare.ui.adapter.UpdateAdapter
import com.kinvey.sample.statusshare.utils.Constants
import kotlinx.android.synthetic.main.fragment_updates_list.*
import timber.log.Timber
import java.util.*

/**
 * Display a persistent list of shared status updates.
 *
 * @author edwardf
 * @since 2.0
 */
class ShareListFragment : KinveyFragment() {
    
    private var dataStore: LinkedDataStore<UpdateEntity>? = null
    
    private var mAdapter: UpdateAdapter? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dataStore = LinkedDataStore(client as AbstractClient<*>, Constants.COL_UPDATES, UpdateEntity::class.java, StoreType.AUTO)
        activity?.invalidateOptionsMenu()
    }

    override val viewID = R.layout.fragment_updates_list

    override fun bindViews(v: View) {
        emptyListText?.visibility = View.GONE
        loadUpdates()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (updateList == null) { return }
        if ((activity as MainActivity).shareList == null) {
            loadUpdates()
        }
    }

    private fun loadUpdates() {
        showListView(false)
        val q =  Query()
            .setLimit(10)
            .addSort(Constants.KMD_LMT_FIELD_NAME, SortOrder.DESC)
        dataStore?.find(q, object : KinveyReadCallback<UpdateEntity> {
            override fun onSuccess(result: KinveyReadResponse<UpdateEntity>?) {
                val list = result?.result ?: listOf()
                Timber.d("Count of updates found: ${ list.size}")
                for (e in list) {
                    Timber.d("result -> $e")
                }
                if (activity == null) { return }
                mainActivity?.shareList = ArrayList(list)
                if (mainActivity?.shareList?.size == 0) {
                    emptyListText?.visibility = View.VISIBLE
                    updateProgress?.visibility = View.GONE
                } else {
                    emptyListText?.visibility = View.GONE
                    setAdapter()
                }
            }
            override fun onFailure(error: Throwable) {
                Timber.w("Error fetching updates data: " + error.message)
                showListView(true)
            }
        }, null)
        // new String[]{"author", "comments", "author"}, 3, true);
    }

    private fun setAdapter() {
        if (mainActivity?.shareList == null) {
            Timber.i("not ready to set Adapter")
            return
        }
        showListView(true)
        mAdapter = UpdateAdapter(activity!!, mainActivity?.shareList ?: listOf(), activity!!.layoutInflater)
        updateList?.adapter = mAdapter
        updateList?.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val item = (activity as MainActivity?)?.shareList?.get(position)
            replaceFragment(UpdateDetailsFragment.newInstance(item), true) 
        }
    }

    private fun showListView(show: Boolean) {
        updateList?.visibility = if (show) View.VISIBLE else View.GONE
        updateProgress?.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_share_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_status_post -> {
                (activity as MainActivity?)!!.replaceFragment(UpdateEditFragment(), true)
                return true
            }
            R.id.menu_refresh -> {
                mAdapter = null
                loadUpdates()
                return true
            }
            R.id.menu_sign_out -> {
                UserStore.logout(client as AbstractClient<BaseUser>, object : KinveyClientCallback<Void?> {
                    override fun onSuccess(result: Void?) {
                        openLoginScreen()
                    }
                    override fun onFailure(error: Throwable) {
                        openLoginScreen()
                    }
                })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openLoginScreen() {
        replaceFragment(LoginFragment(), true)
    }
}