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

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kinvey.android.Client;
import com.kinvey.sample.statusshare.StatusShareApplication;


/**
 *
 *   This abstract class provides hooks for some fragment boilerplate.
 *
 *   It also offers access to the current instance of the Kinvey Client as well as the Roboto font
 *
 * @author edwardf
 * @since 2.0
 */
public abstract class KinveyFragment extends Fragment {

    private Typeface roboto;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        roboto = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Thin.ttf");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {
        View v = inflater.inflate(getViewID(), group, false);
        bindViews(v);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        populateViews();
    }

    /**
     *
     * @return an instance of a Kinvey Client
     */
    public Client getClient(){
        return ((StatusShareApplication) getActivity().getApplicationContext()).getClient();
    }

    /**
     * If you are adding a new fragment, add a new layout.xml file and reference it here.
     * @return the ID defined as R.layout.* for this specific use case fragment.
     */
    public abstract int getViewID();


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
    public abstract void bindViews(View v);

    /**
     * In this method populate the view objects.  This is called from onResume, to ensure that the data displayed is at least refreshed when the fragment is resumed.
     *
     * This method is optional.
     *
     * For example:
     *
     * mytext.setText("hello" + user.getName());
     */
    public void populateViews(){}


    public Typeface getRoboto() {
        return roboto;
    }
}


