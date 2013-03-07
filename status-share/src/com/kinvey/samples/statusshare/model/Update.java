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
package com.kinvey.samples.statusshare.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import com.kinvey.java.model.KinveyMetaData;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author edwardf
 * @since 2.0
 */
public class Update {
    public static final String TAG = Update.class.getSimpleName();
    public static final int MAX_W = 512;
    public static final int MAX_H = 512;

    private String text;

    private String author;
    private String authorName;
    private Bitmap avatar;
    private String since;
    private Bitmap thumbnail;
    private Date date;
    public String lmt;

    public Update() {
        text = null;

        author = null;
        authorName = null;
        avatar = null;
        since = null;
        thumbnail = null;
        date = null;
    }

    public Update(String t, KinveyMetaData.AccessControlList acl, KinveyMetaData md,  Map<String, Friend> friends, Calendar c) {
        setText(t);
        if (md != null) {
            setAuthor(acl.getCreator().toString());
            if (friends != null && friends.get(acl.getCreator()) != null) {
                setAuthorName(friends.get(acl.getCreator()).getName());
                setAvatar(friends.get(acl.getCreator()).getAvatar());
            } else {
                setAuthorName(acl.getCreator().toString());
            }
            setDate(md.getLastModifiedTime());
            setSince(getDate(), c);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        text = t;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String a) {
        author = a;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String a) {
        authorName = a;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap b) {
        avatar = b;
    }

    public String getSince() {
        return since;
    }

    public void setSince(Date d, Calendar c) {

        long secsSince = (c.getTime().getTime() - d.getTime()) / 1000L;

        if (secsSince < 60) {
            since = "now";
        } else if (secsSince < (60 * 60)) {
            since = secsSince / 60 + "m ago";
        } else if (secsSince < (60 * 60 * 24)) {
            since = secsSince / (60 * 60) + "h ago";
        } else {
            since = secsSince / (60 * 60 * 24) + "d ago";
        }
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap t) {
        thumbnail = t;
    }

    public void setThumbnail(String url) {
        thumbnail = null;
//        new GetThumbnailTask().execute(url);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date d) {
        date = d;
    }

    public void setDate(String d) {
        ParsePosition pp = new ParsePosition(0);
        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).parse(d, pp);
    }

//    private class GetThumbnailTask extends AsyncTask<String, Integer, Long> {
//
//        @Override
//        protected Long doInBackground(String... params) {
//            try {
//                BitmapFactory.Options opts = new BitmapFactory.Options();
//                opts.inJustDecodeBounds = true;
//
//                int scaleFactor = 0;
//                String url = params[0];
//                do {
//                    opts.inSampleSize = (int) Math.pow(2, scaleFactor++);
//                    BitmapFactory.decodeStream((InputStream) new URL(url).getContent(), null, opts);
//                    android.util.Log.d(TAG, "opts.inSampleSize: " + opts.inSampleSize + ", opts.outWidth: " + opts.outWidth);
//                } while (opts.outWidth > MAX_W || opts.outHeight > MAX_H);
//
//                opts.inJustDecodeBounds = false;
//                thumbnail = BitmapFactory.decodeStream((InputStream) new URL(url).getContent(), null, opts);
//                android.util.Log.d(TAG, "opts.outWidth: " + opts.outWidth + ", size: " + thumbnail.getHeight() + " x " + thumbnail.getWidth());
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//    }
}
