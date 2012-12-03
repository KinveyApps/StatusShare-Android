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

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Application;

import com.kinvey.KCSClient;
import com.kinvey.KinveySettings;

/**
 * Store global state. In this case, the single instance of KCS.
 *
 */
public class StatusShareApp extends Application {

    public static final String TAG = StatusShareApp.class.getSimpleName();

    private KCSClient service;
    private Calendar appCalendar;

    // Enter your Kinvey app credentials
    private static final String APP_KEY = "kid2338";
    private static final String APP_SECRET = "f5cd03e2bb334b4887f73849cdedb78b";

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        service = KCSClient.getInstance(this.getApplicationContext(), new KinveySettings(APP_KEY, APP_SECRET));

        TimeZone reference = TimeZone.getTimeZone("GMT");
        appCalendar = Calendar.getInstance(reference);
        TimeZone.setDefault(reference);
    }

    public KCSClient getKinveyService() {
        return service;
    }

    public Calendar getAppCalendar() {
        return appCalendar;
    }

}
