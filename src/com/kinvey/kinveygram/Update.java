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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kinvey.KinveyMetadata;


/**
 * Stores information for syncing with Kinvey backend.
 *
 */
public class Update {

    public static final String TAG = Update.class.getSimpleName();

    private String text;

    private String author;
    private String authorName;
    private Bitmap avatar;
    private String since;
    private Bitmap thumbnail;
    private Date date;

    public Update() {
        text = null;

        author = null;
        authorName = null;
        avatar = null;
        since = null;
        thumbnail = null;
        date = null;
    }

    public Update(String t, KinveyMetadata md, Map<String, Friend> friends, Calendar c) {
        setText(t);
        if (md != null) {
            setAuthor(md.getCreatorId());
            if (friends != null && friends.get(md.getCreatorId()) != null) {
                setAuthorName(friends.get(md.getCreatorId()).getName());
                setAvatar(friends.get(md.getCreatorId()).getAvatar());
            } else {
                setAuthorName(md.getCreatorId());
            }
            setDate(md.getLastModifiedTime());
            setSince(getDate(), c);
        }
    }

    public String getText() { return text; }
    public void setText(String t) { text = t; }

    public String getAuthor() { return author; }
    public void setAuthor(String a) { author = a; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String a) { authorName = a; }

    public Bitmap getAvatar() { return avatar; }
    public void setAvatar(Bitmap b) { avatar = b; }

    public String getSince() { return since; }
    public void setSince(Date d, Calendar c) {

        long secsSince = (c.getTime().getTime() - d.getTime()) / 1000L;

        if (secsSince < 60) {
            since = "now";
        } else if (secsSince < (60 * 60)) {
            since = secsSince / 60 + "m";
        } else if (secsSince < (60 * 60 * 24)) {
            since = secsSince / (60 * 60) + "h";
        } else {
            since = secsSince / (60 * 60 * 24) + "d";
        }
    }

    public Bitmap getThumbnail() { return thumbnail; }
    public void setThumbnail(Bitmap t) {
        thumbnail = t;
    }
    public void setThumbnail(String url) {
        thumbnail = null;
        try {
           thumbnail = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
           //android.util.Log.d(TAG, "url: " + url + ", size: " + thumbnail.getHeight() + " x " + thumbnail.getWidth());
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    public Date getDate() { return date; }
    public void setDate(Date d) { date = d; }
    public void setDate(String d) {
        ParsePosition pp = new ParsePosition(0);
        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(d, pp);
    }
}
