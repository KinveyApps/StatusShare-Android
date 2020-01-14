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

import com.google.api.client.util.Key
import com.kinvey.java.linkedResources.LinkedGenericJson
import com.kinvey.java.model.KinveyMetaData
import com.kinvey.java.model.KinveyMetaData.AccessControlList
import com.kinvey.sample.statusshare.utils.Constants
import com.kinvey.sample.statusshare.utils.Constants.ECT_FIELD_NAME
import com.kinvey.sample.statusshare.utils.Constants.KMD_FIELD_NAME

/**
 * This class maintains a Comment which can be persisted with Kinvey.
 *
 * @author edwardf
 * @since 2.0
 */
data class CommentEntity(
    @Key("_id")
    var id: String? = null,
    @Key("text")
    var text: String? = null,
    @Key(Constants.JSON_FIELD_NAME)
    var meta: KinveyMetaData? = KinveyMetaData(),
    @Key("_acl")
    var acl: AccessControlList? = AccessControlList(),
    @Key("author")
    var author: String? = null,
    @Key("updateId")
    var updateId: String? = null
) : LinkedGenericJson() {

    var ect: String? = null
        get() {
            if (field.isNullOrEmpty()) {
                val kmd = get(KMD_FIELD_NAME)
                field = if (kmd is Map<*, *>) {
                    val ect = kmd[ECT_FIELD_NAME]
                    ect?.toString() ?: ""
                } else {
                    null
                }
            }
            return field
        }
}