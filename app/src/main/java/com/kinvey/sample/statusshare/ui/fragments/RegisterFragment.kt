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

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import com.kinvey.android.Client
import com.kinvey.android.model.User
import com.kinvey.android.store.UserStore
import com.kinvey.java.core.KinveyClientCallback
import com.kinvey.sample.statusshare.R
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * @author edwardf
 * @since 2.0
 */
class RegisterFragment : KinveyFragment(), OnClickListener {

    override val viewID: Int
        get() = R.layout.fragment_register

    override fun bindViews(v: View) {
        addEditListeners()
    }

    private val loginStr: String
        get() {
            return registerUsernameEdit?.text?.toString() ?: ""
        }

    private val passStr: String
        get() {
            return registerPasswordEdit?.text?.toString() ?: ""
        }

    private fun addEditListeners() {
        registerCreateAccountBtn?.setOnClickListener(this)
        registerUsernameEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                registerCreateAccountBtn?.isEnabled = validateInput()
            }
        })
        registerUsernameEdit?.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event ->
                if ((actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER))
                  && registerUsernameEdit?.text?.length ?: 0 < MIN_USERNAME_LENGTH) {
                    val text: CharSequence = "User name must contain at least $MIN_USERNAME_LENGTH characters"
                    showToast(text)
                    return@OnEditorActionListener true
                }
                false
            })
        registerPasswordEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                registerCreateAccountBtn?.isEnabled = validateInput()
            }
        })
        registerPasswordEdit?.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event ->
                if ((actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER))
                  && registerPasswordEdit?.text?.length ?: 0 < MIN_USERNAME_LENGTH) {
                    val text: CharSequence = "Password must contain at least $MIN_PASSWORD_LENGTH characters"
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
                    return@OnEditorActionListener true
                }
                false
            })
        registerConfirmPasswordEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                registerCreateAccountBtn?.isEnabled = validateInput()
            }
        })
        registerConfirmPasswordEdit?.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event ->
                if ((actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER))
                        && (registerConfirmPasswordEdit?.text?.length ?: 0 < MIN_USERNAME_LENGTH
                         || registerPasswordEdit?.text.toString() != registerConfirmPasswordEdit?.text.toString())) {
                    val text: CharSequence = "Repeat password must contain at least $MIN_PASSWORD_LENGTH characters and equal password"
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
                    return@OnEditorActionListener true
                }
                false
            })
    }

    private fun validateInput(): Boolean {
        return registerUsernameEdit?.text?.length ?: 0 >= MIN_USERNAME_LENGTH
            && registerPasswordEdit?.text?.length ?: 0 >= MIN_PASSWORD_LENGTH
            && registerConfirmPasswordEdit?.text?.length ?: 0 >= MIN_PASSWORD_LENGTH
            && registerPasswordEdit?.text.toString() == registerConfirmPasswordEdit?.text.toString()
    }

    override fun onClick(v: View) {
        if (v === registerCreateAccountBtn) { submit() }
    }

    private fun submit() {
        UserStore.signUp(loginStr, passStr, client as Client<User>,
        object : KinveyClientCallback<User> {
            override fun onSuccess(result: User) {
                if (activity == null) { return }
                val text: CharSequence = "Welcome ${result["username"]}."
                showToast(text)
                replaceFragment(ShareListFragment(), false)
            }
            override fun onFailure(error: Throwable) {
                if (activity == null) { return }
                val text: CharSequence = "Username already exists."
                showToast(text, true)
            }
        })
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 5
        private const val MIN_PASSWORD_LENGTH = 5
    }
}