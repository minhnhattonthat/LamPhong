package com.lamphongstore.lamphong.data;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lamphongstore.lamphong.activities.MainActivity;
import com.lamphongstore.lamphong.model.AdsItem;
import com.lamphongstore.lamphong.model.NotificationItem;
import com.lamphongstore.lamphong.model.PromotionItem;
import com.lamphongstore.lamphong.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Norvia on 22/03/2017.
 */

public class UserManager {

    private static final UserManager ourInstance = new UserManager();
    private User currentUser = null;
    private ArrayList<AdsItem> adsList;
    private ArrayList<NotificationItem> notificationsList;
    private PromotionItem promotionItem;
    private String deviceToken;
    private Bitmap avatar;

    public UserManager() {
        this.deviceToken = FirebaseInstanceId.getInstance().getToken();
        this.adsList = new ArrayList<>();
        this.notificationsList = new ArrayList<>();
    }

    public static UserManager getInstance() {
        return ourInstance;
    }

    public User getUser() {
        return currentUser;
    }

    void initialize(JSONObject response) throws JSONException {
        currentUser = new User(response);

        cacheUser();
    }

    public void cacheUser() {
        synchronized (this) {
            if (currentUser != null) {
                Gson gson = new Gson();
                String json = gson.toJson(currentUser);
                Log.e("TestJson", json);

                SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
                editor.putString("user_data", json);
                editor.apply();
            }

        }
    }

    private void cacheNotifications() {
        synchronized (this) {
            Gson gson = new Gson();
            String json = gson.toJson(notificationsList);

            SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
            editor.putString("notifications_list", json);
            editor.apply();
        }
    }

    public void cachePromotion() {
        synchronized (this) {
            Gson gson = new Gson();
            String json = gson.toJson(promotionItem);

            SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
            editor.putString("promotion", json);
            editor.apply();
        }
    }

    private void cacheAds() {
        synchronized (this) {
            Gson gson = new Gson();
            String json = gson.toJson(adsList);

            SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
            editor.putString("ads_list", json);
            editor.apply();
        }
    }

    public void loadUserFromLocal() throws JSONException {

        synchronized (this) {
            // read string json from local disk
            String userData = MainActivity.getSharedPreferences().getString("user_data", null);

            // convert string to JSON Object
            Gson gson = new Gson();

            // Get user with JSON Object
            if (userData != null)
                currentUser = gson.fromJson(userData, User.class);
        }
    }

    public void loadNotificationsFromLocal() {
        synchronized (this) {
            String list = MainActivity.getSharedPreferences().getString("notifications_list", null);
            Gson gson = new Gson();
            if (list != null) {
                Type type = new TypeToken<ArrayList<NotificationItem>>() {
                }.getType();
                notificationsList = gson.fromJson(list, type);
                if (notificationsList.size() > 5) {
                    for (int i = 5; i < notificationsList.size(); i++) {
                        notificationsList.remove(i);
                    }
                }
            }
        }
    }

    public void loadPromotionFromLocal() {

        synchronized (this) {
            String item = MainActivity.getSharedPreferences().getString("promotion", null);
            Gson gson = new Gson();
            if (item != null) {
                promotionItem = gson.fromJson(item, PromotionItem.class);
            }
        }
    }


    public void loadAdsListFromLocal() {

        synchronized (this) {
            String list = MainActivity.getSharedPreferences().getString("ads_list", null);
            Gson gson = new Gson();
            if (list != null) {
                Type type = new TypeToken<ArrayList<AdsItem>>() {
                }.getType();
                adsList = gson.fromJson(list, type);
            }
        }
    }

    public void clear() {
        currentUser = null;
        adsList = new ArrayList<>();
        promotionItem = null;
        notificationsList = new ArrayList<>();
        avatar = null;

        AndroidNetworking.evictAllBitmap();

        //Clear login state to sharedPref
        SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
        editor.putString("user_data", null);
        editor.putString("promotion", null);
        editor.putString("notifications_list", null);
        editor.putString("ads_list", null);
        editor.apply();
    }

    public boolean hasCurrentUser() {
        return currentUser != null;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public PromotionItem getPromotionItem() {
        return promotionItem;
    }

    void setPromotionItem(PromotionItem promotionItem) {

        loadPromotionFromLocal();

        if (this.promotionItem != null && this.promotionItem.getId() == promotionItem.getId()) {
            return;
        }

        this.promotionItem = promotionItem;

        cachePromotion();
    }

    public ArrayList<NotificationItem> getNotificationsList() {
        return notificationsList;
    }

    void setNotificationsList(JSONArray notifications) throws JSONException {

        if (notificationsList != null && !notificationsList.isEmpty()) {
            notificationsList.clear();
        }

        for (int i = 0; i < notifications.length(); i++) {
            JSONObject noti = (JSONObject) notifications.get(i);
            notificationsList.add(new NotificationItem(noti));
        }

        cacheNotifications();
    }

    public ArrayList<AdsItem> getAdsList() {
        return adsList;
    }

    void setAdsList(JSONArray adsArray) throws JSONException {

        if (adsList != null && !adsList.isEmpty()) {
            adsList.clear();
        }

        for (int i = 0; i < adsArray.length(); i++) {
            JSONObject jsonObjectAds = (JSONObject) adsArray.get(i);
            adsList.add(new AdsItem(jsonObjectAds));
        }

        cacheAds();
    }

    void deleteNotification(int position) {
        notificationsList.remove(position);

        cacheNotifications();
    }
}
