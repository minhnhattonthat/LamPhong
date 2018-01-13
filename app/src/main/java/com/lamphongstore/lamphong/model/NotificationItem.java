package com.lamphongstore.lamphong.model;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HungNguyen on 4/3/17.
 */
@IgnoreExtraProperties
public class NotificationItem {
    private int id;
    private String created_at;
    private String type;
    private String content;
    private String payloadContent;

    public NotificationItem(JSONObject response) throws JSONException {
        this.created_at = response.getString("created_at");
        this.id = response.getInt("id");
        this.type = response.getString("type");
        this.content = response.getString("content");

        JSONObject objectPayload = response.getJSONObject("payload");
        this.payloadContent = objectPayload.getString("content");
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getPayloadContent() {
        return payloadContent;
    }
}
