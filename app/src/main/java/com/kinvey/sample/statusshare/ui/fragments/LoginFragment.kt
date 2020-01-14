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
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import com.kinvey.android.Client
import com.kinvey.android.model.User
import com.kinvey.android.store.UserStore
import com.kinvey.java.core.KinveyClientCallback
import com.kinvey.sample.statusshare.R
import com.kinvey.sample.statusshare.utils.UiUtils
import kotlinx.android.synthetic.main.fragment_login.*
import timber.log.Timber
import java.io.IOException

/** Allow user to login.
 *
 *
 * @author edwardf
 * @since 2.0
 */
class LoginFragment : KinveyFragment(), OnClickListener {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (client?.isUserLoggedIn == true) {
            Timber.i("logged in: ${client?.activeUser}")
            showToast("Logging in")
            loggedIn()
        }
    }

    override val viewID = R.layout.fragment_login

    override fun bindViews(v: View) {
        loginBtn?.setOnClickListener(this)
        addEditListeners()
    }

    private val loginStr: String
        get() {
            return etLogin?.text?.toString() ?: ""
        }

    private val passStr: String
        get() {
            return etPassword?.text?.toString() ?: ""
        }

    override fun onClick(v: View) {
        if (v === loginBtn) {
            try {
                UserStore.login(loginStr, passStr, client as Client<User>,
                object : KinveyClientCallback<User> {
                    override fun onSuccess(result: User) {
                        if (activity == null) { return }
                        val text = "Logged in ${result["username"]}."
                        showToast(text)
                        loggedIn()
                    }
                    override fun onFailure(error: Throwable) {
                        if (activity == null) { return }
                        val text = "Wrong username or password"
                        showToast(text, true)
                    }
                })
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    private fun addEditListeners() {
        loginBtn?.isEnabled = validateInput()
        etLogin?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable?) { loginBtn?.isEnabled = validateInput() }
        })
        etLogin?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if ((actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE || 
                (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER))
              && etLogin?.text?.length ?: 0 < MIN_USERNAME_LENGTH) {
                val text: CharSequence = "User name must contain at least $MIN_USERNAME_LENGTH characters"
                showToast(text)
                return@OnEditorActionListener true
            }
            false
        })
        etPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable?) { loginBtn?.isEnabled = validateInput() }
        })
        etPassword?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if ((actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE || 
                (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER))
              && etPassword?.text?.length ?: 0 < MIN_USERNAME_LENGTH) {
                val text: CharSequence = "Password must contain at least $MIN_PASSWORD_LENGTH characters"
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
                return@OnEditorActionListener true
            }
            false
        })
    }

    fun validateInput(): Boolean {
        return (etLogin?.text?.length ?: 0 >= MIN_USERNAME_LENGTH
             && etPassword?.text?.length ?: 0 >= MIN_PASSWORD_LENGTH)
    }

    private fun loggedIn() {
        UiUtils.hideKeyboard(activity)
        replaceFragment(ShareListFragment(), false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_login, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_up -> replaceFragment(RegisterFragment(), true)
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val MIN_USERNAME_LENGTH = 4
        const val MIN_PASSWORD_LENGTH = 4
    }
}