package com.lamphongstore.lamphong.model;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HungNguyen on 3/20/17.
 */

@IgnoreExtraProperties
public class AdsItem {
    private ImageResource image;
    private String link_to;

    public AdsItem(JSONObject jsonObjectAds) throws JSONException {
        this.link_to = jsonObjectAds.getString("link_to");

        JSONObject jsonObjectImage = jsonObjectAds.getJSONObject("image");
        this.image = new ImageResource(jsonObjectImage);

    }

    public ImageResource getImage() {
        return image;
    }

    public String getLink_to() {
        return link_to;
    }
}
