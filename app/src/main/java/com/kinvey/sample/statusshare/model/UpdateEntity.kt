/**
 * Copyright (c) 2019 Kinvey Inc.
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
package com.kinvey.sample.statusshare.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.api.client.util.Key
import com.kinvey.java.linkedResources.LinkedGenericJson
import com.kinvey.java.model.KinveyMetaData
import com.kinvey.java.model.KinveyMetaData.AccessControlList
import com.kinvey.java.model.KinveyReference
import com.kinvey.sample.statusshare.utils.Constants
import com.kinvey.sample.statusshare.utils.TimeUtil.getSince
import timber.log.Timber
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class maintains a Status Update which can be persisted with Kinvey.
 *
 * @author edwardf
 * @since 2.0
 */
data class UpdateEntity(var userId: String? = "",
    //----persisted fields
    @Key("_id")
    var id: String? = null,
    @Key("text")
    var text: String? = null,
    @Key(Constants.JSON_FIELD_NAME)
    var meta: KinveyMetaData? = KinveyMetaData(),
    @Key("_acl")
    var acl: AccessControlList? = AccessControlList(),
    @Key("author")
    var author: KinveyReference? = null,
    @Key("comments")
    var comments: ArrayList<KinveyReference>? = null
) : LinkedGenericJson() {

    init {
        putFile(Constants.ATTACHMENT_NAME)
        author = KinveyReference()
        author?.collection = Constants.USER_COLLECTION_NAME
        author?.id = userId
    }

    //-----displayed inferred fields
    var authorName: String? = null
        get() {
            if (author?.resolvedObject != null) {
                field = author?.resolvedObject!!["username"] as String
            }
            return if (field == null) "--" else field
        }
        private set

    var authorID: String? = null
        get() {
            if (author?.resolvedObject != null) {
                field = author?.resolvedObject!!["_id"] as String
            }
            return if (field == null) "" else field
        }
        private set

    var since: String? = null
        get() {
            val pp = ParsePosition(0)
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).parse(meta?.entityCreationTime, pp)
            field = getSince(date, Calendar.getInstance())
            Timber.i("getting since -> ${field != null}")
            return field
        }
        private set//close the output stream//Then decode from the output stream and get the image.//and there is an actual LinkedFile behind the Key//If it hasn't been resolved...

    /**
     * Get the thumbnail from the LinkedResource
     *
     * Note it closes the output stream.
     *
     * @return null or the image attachment
     */
    var thumbnail: Bitmap? = null
        get() {
            //If it hasn't been resolved...
            if (field == null) {
                //and there is an actual LinkedFile behind the Key
                val file = getFile(Constants.ATTACHMENT_NAME)
                if (file != null) {
                    //Then decode from the output stream and get the image.
                    val byteArr = file.output?.toByteArray()
                    field = BitmapFactory.decodeByteArray(byteArr, 0,byteArr?.size ?: 0)
                    try {
                        //close the output stream
                        file.output?.close()
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
            return field
        }
        private set



    fun addComment(newComment: CommentEntity) {
        if (comments == null) {
            comments = ArrayList()
        }
        val ret = KinveyReference(Constants.COL_COMMENTS, newComment.id)
        comments?.add(ret)
    }

    fun resetCommentReferences() {
        if (comments == null) {
            return
        }
        for (c in comments!!) {
            c.remove("_obj")
        }
    }
}