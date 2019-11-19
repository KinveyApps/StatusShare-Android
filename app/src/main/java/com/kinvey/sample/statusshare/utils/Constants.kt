package com.kinvey.sample.statusshare.utils

import com.kinvey.java.model.KinveyMetaData

object Constants {

    const val COL_UPDATES = "Updates"
    const val COL_COMMENTS = "Comments"
    const val USER_COLLECTION_NAME = "users"
    const val JSON_FIELD_NAME = "_meta" //"_acl"
    const val KMD_FIELD_NAME = "_kmd"
    const val ECT_FIELD_NAME = "ect"
    const val ACL_CREATOR_FIELD_NAME = "_acl.creator"
    const val KMD_LMT_FIELD_NAME = "_kmd.lmt"
    const val UPDATE_ID_NAME = "updateId"
    const val USERNAME_FIELD_NAME = "username"

    const val PICK_FROM_CAMERA = 1
    const val PICK_FROM_FILE = 2

    const val SERVER_DATE_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSS"

    const val ATTACHMENT_NAME = "attachment"

    const val ATTACHMENT_FIELD = "attachment"
    const val PUBLIC_FIELD = "_public"
    const val ACL_FIELD = "_acl"
}