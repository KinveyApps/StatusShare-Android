/*
 * Copyright (c) 2012 Kinvey, Inc. All rights reserved.
 *
 * Licensed to Kinvey, Inc. under one or more contributor
 * license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership.  Kinvey, Inc. licenses this file to you under the
 * Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You
 * may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Author: Tom Giesberg
 */

package com.kinvey.kinveygram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.KCSClient;
import com.kinvey.KinveyUser;
import com.kinvey.util.KinveyCallback;

public class LoginActivity extends Activity {
    public static final String TAG = LoginActivity.class.getSimpleName();

    protected static final int MIN_USERNAME_LENGTH = 4;
    protected static final int MIN_PASSWORD_LENGTH = 4;

    protected KCSClient mSharedClient;
    protected Button mButtonSubmit;
    protected EditText mEditUserName;
    protected EditText mEditPassword;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedClient = ((KinveyGramApp) getApplication()).getKinveyService();
        createContent();
        addEditListeners();
    }

    public void createContent() {
        setContentView(R.layout.login);
        mButtonSubmit = (Button) findViewById(R.id.submit);
        mEditUserName = (EditText) findViewById(R.id.userName);
        mEditPassword = (EditText) findViewById(R.id.password);

        // FIXME remove before release
        mEditUserName.setText("droid");
        mEditPassword.setText("droid");
        // end FIXME
    }

    public void addEditListeners() {
        mButtonSubmit.setEnabled(validateInput());

        mEditUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mButtonSubmit.setEnabled(validateInput());
            }
        });

        mEditUserName.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_NEXT
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                    && mEditUserName.getText().length() < MIN_USERNAME_LENGTH
                    ) {

                    CharSequence text = "User name must contain at least " + MIN_USERNAME_LENGTH + " characters";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        mEditPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mButtonSubmit.setEnabled(validateInput());
            }
        });

        mEditPassword.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_NEXT
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                    && mEditPassword.getText().length() < MIN_USERNAME_LENGTH
                    ) {
                    CharSequence text = "Password must contain at least " + MIN_PASSWORD_LENGTH + " characters";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

    }

    public boolean validateInput() {
       return (mEditUserName.toString().length() >= MIN_USERNAME_LENGTH
               && mEditPassword.getText().length() >= MIN_PASSWORD_LENGTH
               );
    }

    public void submit(View view) {
        mSharedClient.loginWithUsername(mEditUserName.getText().toString(), mEditPassword.getText().toString(), new KinveyCallback<KinveyUser>() {
            public void onFailure(Throwable t) {
                CharSequence text = "Wrong username or password. Please check and try again.";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }

            public void onSuccess(KinveyUser u) {
                CharSequence text = "Welcome back," + u.getUsername() + ".";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, UpdatesActivity.class));
                LoginActivity.this.finish();
            }

        });
    }

    public void goToCreateAccount(View view) {
        startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
        finish();
    }
}
