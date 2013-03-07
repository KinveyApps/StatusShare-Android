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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;
import com.kinvey.android.Client;
import com.kinvey.samples.statusshare.StatusShare;
import com.kinvey.samples.statusshare.StatusShareApplication;
import com.kinvey.samples.statusshare.model.Update;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**  Boilerplate for Android Fragments, including wrapper to Applications Kinvey AbstractClient.
 *
 *
 *
 * @author edwardf
 * @since 2.0
 */
public abstract class KinveyFragment extends SherlockFragment{


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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

    public Client getClient(){
        return ((StatusShareApplication) getSherlockActivity().getApplicationContext()).getClient();
    }

    public Calendar getCalendar() {
        TimeZone reference = TimeZone.getTimeZone("GMT");
        TimeZone.setDefault(reference);
        return Calendar.getInstance(reference);
    }

    public List<Update> getUpdates(){
        return ((StatusShare)getSherlockActivity()).getmUpdates();
    }
    public void setUpdates(List<Update> ups){
        ((StatusShare)getSherlockActivity()).setmUpdates(ups);
    }

    /**
     * @return the ID defined as  R.layout.* for this specific use case fragment.  If you are adding a new fragment, add a new layout.xml file and reference it here.
     */
    public abstract int getViewID();


    /**
     * In this method establish all references to View widgets within the layout.
     *
     * For example:
     *
     * TextView mytext = (TextView) v.findViewById(R.id.mytextview);
     *
     * This is called once from onCreate.
     *
     * @param v  the View object inflated by the Fragment, this will be the parent of any View Widget within the fragment.
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





}


