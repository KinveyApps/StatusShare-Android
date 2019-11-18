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
package com.kinvey.sample.statusshare.model;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.api.client.util.Key;
import com.kinvey.android.Client;
import com.kinvey.java.linkedResources.LinkedGenericJson;
import com.kinvey.java.model.KinveyMetaData;
import com.kinvey.java.model.KinveyReference;
import com.kinvey.sample.statusshare.utils.Constants;
import com.kinvey.sample.statusshare.ui.MainActivity;

/**
 * This class maintains a Status Update which can be persisted with Kinvey.
 *
 * @author edwardf
 * @since 2.0
 */
public class UpdateEntity extends LinkedGenericJson {

    //----persisted fields
    @Key("_id")
    private String id;
    @Key("text")
    private String text;
    @Key(Constants.JSON_FIELD_NAME)
    private KinveyMetaData meta;
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl;
    @Key("author")
    private KinveyReference author;
    @Key("comments")
    private ArrayList<KinveyReference> comments;

    public static final int MAX_W = 512;
    public static final int MAX_H = 512;
    public static final String attachmentName = "attachment";

    //-----displayed inferred fields
    private String authorName;
    private String authorID;
    private String since;
    private Bitmap thumbnail;

    public UpdateEntity(){
        putFile(attachmentName);
    }


    public UpdateEntity(String userid) {
        id = null;
        text = null;
        meta = new KinveyMetaData();
        acl = new KinveyMetaData.AccessControlList();
        putFile(attachmentName);
        author = new KinveyReference();
        author.setCollection(Constants.USER_COLLECTION_NAME);
        author.setId(userid);
    }

    public void setAuthor(KinveyReference author){
        this.author = author;
    }

    public KinveyReference getAuthor(){
        return this.author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public KinveyMetaData.AccessControlList getAcl() {
        return acl;
    }

    public void setAcl(KinveyMetaData.AccessControlList acl) {
        this.acl = acl;
    }

    public KinveyMetaData getMeta() {
        return meta;
    }

    public void setMeta(KinveyMetaData meta) {
        this.meta = meta;
    }

    public String getAuthorID(){
        if (this.author != null && this.author.getResolvedObject() != null){
            this.authorID = (String) (this.author.getResolvedObject()).get("_id");
        }
        return ((this.authorID == null) ? "" : this.authorID);
    }

    public String getAuthorName() {
        if (this.author != null && this.author.getResolvedObject() != null){
            this.authorName = (String) (this.author.getResolvedObject()).get("username");
        }
        return ((this.authorName == null) ? "--" : this.authorName);
    }

    public String getSince() {
        ParsePosition pp = new ParsePosition(0);
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).parse(this.meta.getEntityCreationTime(), pp);
        since = MainActivity.getSince(date, Calendar.getInstance());

        Log.i(Client.TAG,  "getting since -> " + (since != null));
        return since;
    }

    /**
     * Get the thumbnail from the LinkedResource
     *
     * Note it closes the output stream.
     *
     * @return null or the image attachment
     */
    public Bitmap getThumbnail() {
        //If it hasn't been resolved...
        if (thumbnail == null) {
            //and there is an actual LinkedFile behind the Key
            if (getFile(attachmentName) != null) {
                //Then decode from the output stream and get the image.
                thumbnail = BitmapFactory.decodeByteArray(getFile(attachmentName).getOutput().toByteArray(),
                        0, getFile(attachmentName).getOutput().toByteArray().length);
                try {
                    //close the output stream
                    getFile(attachmentName).getOutput().close();
                } catch (Exception e) {
                }
            }
        }
        return thumbnail;
    }

    public ArrayList<KinveyReference> getComments() {
        return comments;
    }

    public void setComments(ArrayList<KinveyReference> comments) {
        this.comments = comments;
    }

    public void addComment(CommentEntity newComment){
        if (this.comments == null){
            this.comments = new ArrayList<KinveyReference>();
        }
        KinveyReference ret = new KinveyReference(Constants.COL_COMMENTS, newComment.getId());
        this.comments.add(ret);
    }

    public void resetCommentReferences(){
        if (this.comments == null){
             return;
        }
        for (KinveyReference c : comments){
            c.remove("_obj");

        }
    }
}
