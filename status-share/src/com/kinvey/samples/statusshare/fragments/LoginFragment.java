/*
* Copyright (c) 2013, Kinvey, Inc. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed under the License
* is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
* or implied. See the License for the specific language governing permissions and limitations under
* the License.
*/
package com.kinvey.samples.statusshare.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.samples.statusshare.R;
import com.kinvey.samples.statusshare.StatusShare;

/** Allow user to login.
 *
 *
* @author edwardf
* @since 2.0
*/
public class LoginFragment extends KinveyFragment implements View.OnClickListener{

    private EditText username;
    private EditText password;
    private Button login;

    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MIN_PASSWORD_LENGTH = 4;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getClient().user().isUserLoggedIn()){
            Log.i(StatusShare.TAG, "logged in: ");
            Toast.makeText(getSherlockActivity(), "Logging in", Toast.LENGTH_SHORT);
            loggedIn();

        }
    }



    @Override
    public int getViewID() {
        return R.layout.login;
    }

    @Override
    public void bindViews(View v) {
        username = (EditText) v.findViewById(R.id.et_login);
        password = (EditText) v.findViewById(R.id.et_password);
        login = (Button) v.findViewById(R.id.login);
        login.setOnClickListener(this);
    }





    @Override
    public void onClick(View v) {
        if (v == login){
            getClient().user().login(username.getText().toString(), password.getText().toString(), new KinveyUserCallback() {
                @Override
                public void onSuccess(User result) {
                    CharSequence text = "Logged in " + result.get("username") + ".";
                    Toast.makeText(getSherlockActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    loggedIn();
                }

                public void onFailure(Throwable t) {
                    CharSequence text = "Wrong username or password";
                    Toast toast = Toast.makeText(getSherlockActivity().getApplicationContext(), text, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            });


        }
    }

    protected void addEditListeners() {
        login.setEnabled(validateInput());

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                login.setEnabled(validateInput());
            }
        });

        username.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_NEXT
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || (event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                                && username.getText().length() < MIN_USERNAME_LENGTH
                                ) {

                            CharSequence text = "User name must contain at least " + MIN_USERNAME_LENGTH + " characters";
                            Toast.makeText(getSherlockActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                login.setEnabled(validateInput());
            }
        });

        password.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_NEXT
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || (event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                                && password.getText().length() < MIN_USERNAME_LENGTH
                                ) {
                            CharSequence text = "Password must contain at least " + MIN_PASSWORD_LENGTH + " characters";
                            Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });



    }

    public boolean validateInput() {
        return (username.toString().length() >= MIN_USERNAME_LENGTH
                && password.getText().length() >= MIN_PASSWORD_LENGTH
        );
    }

    private void loggedIn(){

        ((StatusShare) getSherlockActivity()).replaceFragment(new ShareListFragment(), false);


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.login, menu);
    }
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_sign_up:
                ((StatusShare) getSherlockActivity()).replaceFragment(new RegisterFragment(), true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}


