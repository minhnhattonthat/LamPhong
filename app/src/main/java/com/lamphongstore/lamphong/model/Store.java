package com.lamphongstore.lamphong.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bipug on 3/16/17.
 */
public class Store {
    private int id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private ImageResource image;
    private double latitude;
    private double longtitude;
    private ArrayList<ImageResource> images;
    public Store(JSONObject response) throws JSONException {
        this.id = response.getInt("id");
        this.address = response.getString("address");
        this.name = response.getString("name");
        this.city = response.getString("city");
        this.phone = response.getString("phone");
        this.latitude = response.getDouble("lat");
        this.longtitude = response.getDouble("lng");

        JSONObject image = response.getJSONObject("image");
        this.image = new ImageResource(image);

        JSONArray images = response.getJSONArray("images");
        for (int i = 0; i < images.length() ; i ++){
            ImageResource imageResource = new ImageResource(images.getJSONObject(i));
            Log.e("Store",imageResource.getImgUrl());
            if(this.images == null) this.images = new ArrayList<>();
            this.images.add(imageResource);
        }
    }

    public ArrayList<ImageResource> getImages() {
        return images;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public ImageResource getImage() {
        return image;
    }
}