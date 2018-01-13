package com.lamphongstore.lamphong;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by HungNguyen on 3/24/17.
 */

public class ApiLamPhong {

    public static String API_PUT_DEVICE_TOKEN;
    public static String API_LOGIN;
    public static String API_REGISTER;
    public static String API_CHANGE_PROFILE;
    public static String API_CHANGE_PASSWORD;
    public static String API_CURRENT_USER;
    public static String API_ALL_LEVEL;
    public static String API_ADD_POINT;
    public static String API_LIST_ADS;
    public static String API_PROMOTION;
    public static String API_LIST_STORE;
    public static String API_UPLOAD_IMAGE;
    public static String API_RESET_PASSWORD;
    public static String RULE_MEMBER_INFORMATION;
    public static String SALES_INFORMATION;
    public static String API_LOGOUT;
    public static String AUTHORIZATION;
    public static String DEVICE_TOKEN;
    public static String API_LIST_NOTIFICATION;
    public static String API_DEL_NOTIFICATION;
    public static String SHARE_CODE_INFORMATION;
    private static String DOMAIN_LAMPHONG;

    public static void setup(Context context) {
        if (BuildConfig.DEBUG) {
            Log.e("Version", "Dev");
            DOMAIN_LAMPHONG = "http://lamphongdev.skylab.vn";
        } else {
            DOMAIN_LAMPHONG = "http://lamphong.skylab.vn";
            Fabric.with(context, new Crashlytics());
        }
        SHARE_CODE_INFORMATION = DOMAIN_LAMPHONG + "/share";
        API_PUT_DEVICE_TOKEN = DOMAIN_LAMPHONG + "/api/user/device_token";
        API_LOGIN = DOMAIN_LAMPHONG + "/api/user/login";
        API_REGISTER = DOMAIN_LAMPHONG + "/api/user/register";
        API_CHANGE_PROFILE = DOMAIN_LAMPHONG + "/api/user/profile";
        API_CHANGE_PASSWORD = DOMAIN_LAMPHONG + "/api/user/change_password";
        API_CURRENT_USER = DOMAIN_LAMPHONG + "/api/user/profile";
        API_ALL_LEVEL = DOMAIN_LAMPHONG + "/api/user_levels";
        API_ADD_POINT = DOMAIN_LAMPHONG + "/api/user/add_point";
        API_LIST_ADS = DOMAIN_LAMPHONG + "/api/ads";
        API_PROMOTION = DOMAIN_LAMPHONG + "/api/promotions";
        API_LIST_STORE = DOMAIN_LAMPHONG + "/api/stores";
        API_UPLOAD_IMAGE = DOMAIN_LAMPHONG + "/api/image/upload";
        API_RESET_PASSWORD = DOMAIN_LAMPHONG + "/api/user/reset_password";
        RULE_MEMBER_INFORMATION = DOMAIN_LAMPHONG + "/rules";
        SALES_INFORMATION = "http://www.lamphongstore.com/tin-tuc/tin-su-kien-khuyen-mai";
        API_LOGOUT = DOMAIN_LAMPHONG + "/api/user/logout";
        AUTHORIZATION = "Authorization";
        DEVICE_TOKEN = "device_token";
        API_LIST_NOTIFICATION = DOMAIN_LAMPHONG + "/api/notification/list";
        API_DEL_NOTIFICATION = DOMAIN_LAMPHONG + "/api/notification/delete/";
    }
}
