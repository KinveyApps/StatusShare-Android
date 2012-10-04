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

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.kinvey.KinveyMetadata;
import com.kinvey.persistence.mapping.MappedEntity;
import com.kinvey.persistence.mapping.MappedField;

/**
 * Stores information for syncing with Kinvey backend.
 *
 */
public class UpdateEntity implements MappedEntity {

    public static final String TAG = UpdateEntity.class.getSimpleName();

    private String id;
    private String text;
    private KinveyMetadata meta;
    private JSONObject attachment;

    public UpdateEntity() {
        id = null;
        text = null;
        meta = null;
        attachment = null;
    }

    @Override
    public List<MappedField> getMapping() {
        return Arrays.asList(new MappedField[] {
            new MappedField("id", "_id")
            , new MappedField("text", "text")
            , new MappedField("meta", KinveyMetadata.FIELD_NAME)
            , new MappedField("attachment", "attachment")
            });
    }

    // Getters and setters for all fields are required
    public String getId() { return id; }
    public void setId(String i) { id = i; }

    public String getText() { return text; }
    public void setText(String t) { text = t; }

    public KinveyMetadata getMeta() { return meta; }
    public void setMeta(KinveyMetadata m) { meta = m; }

    public JSONObject getAttachment() { return attachment; }
    public void setAttachment(JSONObject a) {
        attachment = a;
    }
}
