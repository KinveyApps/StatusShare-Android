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

package com.kinvey.statusshare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.KinveyUser;
import com.kinvey.statusshare.R;
import com.kinvey.util.KinveyCallback;

public class CreateAccountActivity extends BaseActivity {
    public static final String TAG = CreateAccountActivity.class.getSimpleName();

    private EditText mEditRepeatPassword;
	private Button mButtonSubmit;
	private EditText mEditUserName;
	private EditText mEditPassword;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_account);
        mButtonSubmit = (Button) findViewById(R.id.create_account);
        mEditUserName = (EditText) findViewById(R.id.et_login);
        mEditPassword = (EditText) findViewById(R.id.et_password);
        mEditRepeatPassword = (EditText) findViewById(R.id.confirm_password);
        this.addEditListeners();
    }


    private void addEditListeners() {

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
                    && mEditUserName.getText().length() < LoginActivity.MIN_USERNAME_LENGTH
                    ) {

                    CharSequence text = "User name must contain at least " + LoginActivity.MIN_USERNAME_LENGTH + " characters";
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
                    && mEditPassword.getText().length() < LoginActivity.MIN_USERNAME_LENGTH
                    ) {
                    CharSequence text = "Password must contain at least " + LoginActivity.MIN_PASSWORD_LENGTH + " characters";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    	
    	
        mEditRepeatPassword.addTextChangedListener(new TextWatcher() {
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

        mEditRepeatPassword.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_NEXT
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                    && (mEditRepeatPassword.getText().length() < LoginActivity.MIN_USERNAME_LENGTH
                        || !mEditPassword.getText().toString().equals(mEditRepeatPassword.getText().toString())
                    )) {
                    CharSequence text = "Repeat password must contain at least " + LoginActivity.MIN_PASSWORD_LENGTH + " characters and equal password";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    private boolean validateInput() {
       return (mEditUserName.toString().length() >= LoginActivity.MIN_USERNAME_LENGTH
               && mEditPassword.getText().length() >= LoginActivity.MIN_PASSWORD_LENGTH
               && mEditRepeatPassword.getText().length() >= LoginActivity.MIN_PASSWORD_LENGTH
               && mEditPassword.getText().toString().equals(mEditRepeatPassword.getText().toString()));
    }

    @Override
    public void onBackPressed() {
    	startActivity(new Intent(this, LoginActivity.class));
    	super.onBackPressed();
    }
    
    public void submit(View view) {
            mKinveyClient.createUserWithUsername(mEditUserName.getText().toString(), mEditPassword.getText().toString(), new KinveyCallback<KinveyUser>() {
                public void onFailure(Throwable t) {
                    CharSequence text = "Username already exists.";
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }

                public void onSuccess(KinveyUser u) {
                    CharSequence text = "Welcome " + u.getUsername() + ".";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    CreateAccountActivity.this.startActivity(new Intent(CreateAccountActivity.this, HomeActivity.class));
                    CreateAccountActivity.this.finish();
                }

            });

    }


}
