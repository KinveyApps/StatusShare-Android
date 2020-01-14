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
package com.kinvey.sample.statusshare

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.api.client.http.HttpTransport
import com.kinvey.android.Client
import com.kinvey.android.Client.Builder
import com.kinvey.android.model.User
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author edwardf
 * @since 2.0
 */
class App : Application() {

    var client: Client<User>? = null
        get() {
            if (field == null) {
                field = Builder<User>(this).build()
            }
            return field
        }
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        // run the following comamnd to turn on verbose logging:
        // adb shell setprop log.tag.HttpTransport DEBUG
        Logger.getLogger(HttpTransport::class.java.name).level = LOGGING_LEVEL
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    companion object {
        var instance: App? = null
        private val LOGGING_LEVEL: Level? = Level.FINEST
    }
}