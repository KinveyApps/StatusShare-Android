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

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import com.kinvey.java.model.KinveyMetaData;


/**
 * This class maintains a Status Update which can be persisted with Kinvey.
 *
 * @author edwardf
 * @since 2.0
 */
public class UpdateEntity extends GenericJson {

    @Key("_id")
    private String id;
    @Key("text")
    private String text;
    @Key(KinveyMetaData.JSON_FIELD_NAME)
    private KinveyMetaData meta;
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl;

    public UpdateEntity() {
        id = null;
        text = null;
        meta = new KinveyMetaData();
        acl = new KinveyMetaData.AccessControlList();

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
}
