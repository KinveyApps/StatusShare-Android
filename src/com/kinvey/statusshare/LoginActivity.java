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

package com.kinvey.statusshare;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

	private static final String USER_DETAILS = "userdetails";
	private static final String SIGNED_IN_PREF = "signedIn";
	private static final String PASS_PREF = "passwd";
	private static final String USERNAME_PREF = "username";
	public static final String LOGGED_OUT = "loggedOut";

    protected KCSClient mKinveyClient;
    protected Button mButtonSubmit;
    protected EditText mEditUserName;
    protected EditText mEditPassword;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null && intent.getExtras().getBoolean(LOGGED_OUT)) {
        	Log.d(TAG, "fogetting user");
        	forgetUserAndLogout();
        }
        
        mKinveyClient = ((StatusShareApp) getApplication()).getKinveyService();
        
        SharedPreferences userdetails = getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        boolean hasSignedIn = userdetails.getBoolean(SIGNED_IN_PREF, false);
        if (hasSignedIn){
            String username = userdetails.getString(USERNAME_PREF, "unknown");
            String pass = userdetails.getString(PASS_PREF, "unknown");
            mKinveyClient.loginWithUsername(username, pass, new KinveyCallback<KinveyUser>(){
                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "failed to log in to kinvey", t);
                }
                @Override
                public void onSuccess(KinveyUser arg0) {
                    Log.d(TAG, "logged into kinvey");
                    startUpdateActivity();
                }
            });
        } else {    
	        createContent();        
	        addEditListeners();
	    }
    }

    public void createContent() {
        setContentView(R.layout.login);
        mButtonSubmit = (Button) findViewById(R.id.submit);
        mEditUserName = (EditText) findViewById(R.id.userName);
        mEditPassword = (EditText) findViewById(R.id.password);
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
        mKinveyClient.loginWithUsername(mEditUserName.getText().toString(), mEditPassword.getText().toString(), new KinveyCallback<KinveyUser>() {
            public void onFailure(Throwable t) {
                CharSequence text = "Wrong username or password. Please check and try again.";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }

            public void onSuccess(KinveyUser u) {
                CharSequence text = "Welcome back," + u.getUsername() + ".";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                startUpdateActivity();
                saveKinveyLoginDetails(u);
            }

        });
    }

    public void goToCreateAccount(View view) {
        startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
        finish();
    }

    private void startUpdateActivity() {
    	LoginActivity.this.startActivity(new Intent(LoginActivity.this, UpdatesActivity.class));
    	LoginActivity.this.finish();
    }

    private void forgetUserAndLogout() {
        SharedPreferences userdetails = getSharedPreferences(USER_DETAILS, Application.MODE_PRIVATE);
        SharedPreferences.Editor userDetailsEdit = userdetails.edit();
        userDetailsEdit.putBoolean(SIGNED_IN_PREF, false);
        userDetailsEdit.putString(PASS_PREF, null);
        userDetailsEdit.putString(USERNAME_PREF, null);
        userDetailsEdit.commit();
    }
    
    private void saveKinveyLoginDetails(KinveyUser u) {
        SharedPreferences userdetails = getSharedPreferences(USER_DETAILS, Application.MODE_PRIVATE);
        SharedPreferences.Editor userDetailsEdit = userdetails.edit();
        userDetailsEdit.putBoolean(SIGNED_IN_PREF, true);
        userDetailsEdit.putString(PASS_PREF, u.getPassword());
        userDetailsEdit.putString(USERNAME_PREF, u.getUsername());
        userDetailsEdit.commit();
    }
}
