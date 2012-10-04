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

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Friend {

    private String id;
    private String name;
    private Bitmap avatar;

    public Friend() {
        id = null;
        name = null;
        avatar = null;
    }

    public Friend(String i, String n) {
        setId(i);
        setName(n);
    }

    public String getId() { return id; }
    public void setId(String i) { id = i; }

    public String getName() { return name; }
    public void setName(String n) {
        name = n;
        setAvatar(n);
    }

    public Bitmap getAvatar() { return avatar; }
    public void setAvatar(Bitmap b) { avatar = b; }
    public void setAvatar(String gravatarID) {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] digest = digester.digest(gravatarID.getBytes());

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }

            String url = new String("http://www.gravatar.com/avatar/" + sb.toString() + ".jpg?d=identicon");
            //android.util.Log.d(TAG, gravatarID + " = " + url);
            avatar = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
