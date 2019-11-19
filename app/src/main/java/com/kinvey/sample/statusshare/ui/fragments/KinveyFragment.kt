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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kinvey.android.Client
import com.kinvey.android.model.User
import com.kinvey.sample.statusshare.App
import com.kinvey.sample.statusshare.ui.MainActivity

/**
 *
 * This abstract class provides hooks for some fragment boilerplate.
 *
 * It also offers access to the current instance of the Kinvey Client as well as the Roboto font
 *
 * @author edwardf
 * @since 2.0
 */
abstract class KinveyFragment : Fragment() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View? {
        return inflater.inflate(viewID, group, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
    }

    override fun onResume() {
        super.onResume()
        populateViews()
    }

    val mainActivity : MainActivity?
        get() {
            return activity as MainActivity?
        }

    fun replaceFragment(frag: Fragment, addToBackStack: Boolean) {
        (activity as MainActivity?)?.replaceFragment(frag, addToBackStack)
    }

    fun showToast(text: CharSequence, showCentered: Boolean = false) {
        val toast = Toast.makeText(activity?.applicationContext, text, Toast.LENGTH_LONG)
        if (showCentered) {
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        }
        toast.show()
    }

    /**
     *
     * @return an instance of a Kinvey Client
     */
    val client: Client<User>?
        get() = App.instance?.client

    /**
     * If you are adding a new fragment, add a new layout.xml file and reference it here.
     * @return the ID defined as R.layout.* for this specific use case fragment.
     */
    abstract val viewID: Int

    /**
     * In this method establish all references to View widgets within the layout.
     *
     * For example:
     *
     * TextView mytext = (TextView) v.findViewById(R.id.mytextview);
     *
     * This is called once from onCreateView.
     *
     * @param v  the View object inflated by the Fragment, this will be the parent of any View within the fragment.
     */
    abstract fun bindViews(v: View)

    /**
     * In this method populate the view objects.  This is called from onResume, to ensure that the data displayed is at least refreshed when the fragment is resumed.
     *
     * This method is optional.
     *
     * For example:
     *
     * mytext.setText("hello" + user.getName());
     */
    open fun populateViews() {}
}