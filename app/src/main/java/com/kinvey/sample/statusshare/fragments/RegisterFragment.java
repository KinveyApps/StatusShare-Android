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
package com.kinvey.sample.statusshare.fragments;

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
import com.kinvey.android.model.User;
import com.kinvey.android.store.UserStore;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.sample.statusshare.MainActivity;
import com.kinvey.sample.statusshare.R;

/**
 * @author edwardf
 * @since 2.0
 */
public class RegisterFragment extends KinveyFragment implements View.OnClickListener {

    private EditText confirmPassword;
    private Button registerButton;
    private EditText userName;
    private EditText password;

    private TextView usernameLabel;
    private TextView passwordLabel;
    private TextView confirmPasswordLabel;

    private static final int MIN_USERNAME_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 5;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getViewID() {
        return R.layout.fragment_register;
    }

    @Override
    public void bindViews(View v) {
        registerButton = (Button) v.findViewById(R.id.register_create_account);
        userName = (EditText) v.findViewById(R.id.register_username);
        password = (EditText) v.findViewById(R.id.register_password);
        confirmPassword = (EditText) v.findViewById(R.id.register_confirm_password);
        usernameLabel = (TextView) v.findViewById(R.id.register_username_label);
        passwordLabel = (TextView) v.findViewById(R.id.register_password_label);
        confirmPasswordLabel = (TextView) v.findViewById(R.id.register_confirm_label);

        usernameLabel.setTypeface(getRoboto());
        passwordLabel.setTypeface(getRoboto());
        confirmPasswordLabel.setTypeface(getRoboto());
        registerButton.setTypeface(getRoboto());
        userName.setTypeface(getRoboto());
        password.setTypeface(getRoboto());
        confirmPassword.setTypeface(getRoboto());

        this.addEditListeners();

    }

    private void addEditListeners() {

        registerButton.setOnClickListener(this);

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                registerButton.setEnabled(validateInput());
            }
        });

        userName.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_NEXT
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || (event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                                && userName.getText().length() < MIN_USERNAME_LENGTH
                                ) {

                            CharSequence text = "User name must contain at least " + MIN_USERNAME_LENGTH + " characters";
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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
                registerButton.setEnabled(validateInput());
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
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                registerButton.setEnabled(validateInput());
            }
        });

        confirmPassword.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_NEXT
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || (event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                                && (confirmPassword.getText().length() < MIN_USERNAME_LENGTH
                                || !password.getText().toString().equals(confirmPassword.getText().toString())
                        )) {
                            CharSequence text = "Repeat password must contain at least " + MIN_PASSWORD_LENGTH + " characters and equal password";
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });
    }

    private boolean validateInput() {
        return (userName.toString().length() >= MIN_USERNAME_LENGTH
                && password.getText().length() >= MIN_PASSWORD_LENGTH
                && confirmPassword.getText().length() >= MIN_PASSWORD_LENGTH
                && password.getText().toString().equals(confirmPassword.getText().toString()));
    }

    @Override
    public void onClick(View v) {
        if (v == registerButton){
            submit();
        }
    }

    public void submit() {
        UserStore.signUp(userName.getText().toString(), password.getText().toString(), getClient(), new KinveyClientCallback<User>() {
            public void onSuccess(User result) {
                if (getActivity() == null){
                    return;
                }
                CharSequence text = "Welcome " + result.get("username")+ ".";
                Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
                ((MainActivity) getActivity()).replaceFragment(new ShareListFragment(), false);
            }
            public void onFailure(Throwable error) {
                if (getActivity() == null){
                    return;
                }
                CharSequence text = "Username already exists.";
                Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });
    }
}
