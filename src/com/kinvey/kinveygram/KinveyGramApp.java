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

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Application;

import com.kinvey.KCSClient;
import com.kinvey.KinveySettings;

/**
 * Store global state. In this case, the single instance of KCS.
 *
 */
public class KinveyGramApp extends Application {

    public static final String TAG = KinveyGramApp.class.getSimpleName();

    private KCSClient service;
    private Calendar appCalendar;

    // Enter your Kinvey app credentials

// FIXME before releasing
//    private static final String APP_KEY = "your_app_key";
//    private static final String APP_SECRET = "your_app_secret";

// KinveyGram
    private static final String APP_KEY = "kid2338";
    private static final String APP_SECRET = "f5cd03e2bb334b4887f73849cdedb78b";

// KinveyGram2
//    private static final String APP_KEY = "kid_PVB8IV0f0";
//    private static final String APP_SECRET = "ef6006120da444d5b8d849c74e03aa8a";

// KinveyGram3
//    private static final String APP_KEY = "kid_TTLKLIe2N";
//    private static final String APP_SECRET = "1c5df31e91554bdabf9cba77db4417a9";

    @Override
    public void onCreate() {
        super.onCreate();
        //android.util.Log.d(TAG, "onCreate");
        initialize();
    }

    private void initialize() {
        // Enter your app credentials here
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
