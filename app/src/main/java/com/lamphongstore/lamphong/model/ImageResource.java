package com.lamphongstore.lamphong.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by bipug on 3/18/17.
 */
@IgnoreExtraProperties
public class ImageResource implements Serializable {
    private int height;
    private int width;
    @SerializedName("url")
    private String imgUrl;

    public ImageResource(JSONObject image) throws JSONException {
        this.width = image.getInt("width");
        this.height = image.getInt("height");
        this.imgUrl = image.getString("url");
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String url) {
        imgUrl = url;
    }
}
