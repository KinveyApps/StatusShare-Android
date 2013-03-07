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
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.samples.statusshare.R;
import com.kinvey.samples.statusshare.StatusShare;


/**
 * @author edwardf
 * @since 2.0
 */
public class RegisterFragment extends KinveyFragment implements View.OnClickListener {


    private EditText mEditRepeatPassword;
    private Button mButtonSubmit;
    private EditText mEditUserName;
    private EditText mEditPassword;

    private static final int MIN_USERNAME_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 5;


    @Override
    public int getViewID() {
        return R.layout.create_account;
    }

    @Override
    public void bindViews(View v) {
        mButtonSubmit = (Button) v.findViewById(R.id.create_account);
        mEditUserName = (EditText) v.findViewById(R.id.et_login);
        mEditPassword = (EditText) v.findViewById(R.id.et_password);
        mEditRepeatPassword = (EditText) v.findViewById(R.id.confirm_password);

        this.addEditListeners();

    }


    private void addEditListeners() {

        mButtonSubmit.setOnClickListener(this);

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
                            Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_SHORT).show();
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
                                && (mEditRepeatPassword.getText().length() < MIN_USERNAME_LENGTH
                                || !mEditPassword.getText().toString().equals(mEditRepeatPassword.getText().toString())
                        )) {
                            CharSequence text = "Repeat password must contain at least " + MIN_PASSWORD_LENGTH + " characters and equal password";
                            Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });
    }

    private boolean validateInput() {
        return (mEditUserName.toString().length() >= MIN_USERNAME_LENGTH
                && mEditPassword.getText().length() >= MIN_PASSWORD_LENGTH
                && mEditRepeatPassword.getText().length() >= MIN_PASSWORD_LENGTH
                && mEditPassword.getText().toString().equals(mEditRepeatPassword.getText().toString()));
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonSubmit){
            submit();

        }

    }

    public void submit() {
        getClient().user().create(mEditUserName.getText().toString(), mEditPassword.getText().toString(), new KinveyUserCallback() {
            public void onFailure(Throwable t) {
                CharSequence text = "Username already exists.";
                Toast toast = Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }

            public void onSuccess(User u) {
                CharSequence text = "Welcome " + u.get("username")+ ".";
                Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_LONG).show();
                ((StatusShare) getSherlockActivity()).replaceFragment(new ShareListFragment(), false);
            }

        });

    }

}
