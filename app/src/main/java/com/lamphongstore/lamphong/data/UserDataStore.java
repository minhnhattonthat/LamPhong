package com.lamphongstore.lamphong.data;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lamphongstore.lamphong.model.LPError;
import com.lamphongstore.lamphong.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.lamphongstore.lamphong.ApiLamPhong.API_CHANGE_PROFILE;
import static com.lamphongstore.lamphong.ApiLamPhong.API_CURRENT_USER;
import static com.lamphongstore.lamphong.ApiLamPhong.API_LOGIN;
import static com.lamphongstore.lamphong.ApiLamPhong.API_LOGOUT;
import static com.lamphongstore.lamphong.ApiLamPhong.API_PUT_DEVICE_TOKEN;
import static com.lamphongstore.lamphong.ApiLamPhong.API_REGISTER;
import static com.lamphongstore.lamphong.ApiLamPhong.API_UPLOAD_IMAGE;
import static com.lamphongstore.lamphong.ApiLamPhong.AUTHORIZATION;
import static com.lamphongstore.lamphong.ApiLamPhong.DEVICE_TOKEN;

/**
 * Created by HungNguyen on 3/29/17.
 */

public class UserDataStore {

    private OnResponseListener mOnResponseListener;

    public UserDataStore() {

    }

    public void setOnResponseListener(OnResponseListener listener) {
        this.mOnResponseListener = listener;
    }

    public void register(Map<String, String> params) {
        AndroidNetworking.post(API_REGISTER)
                .addBodyParameter(params)
                .setTag("register")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                UserManager.getInstance().initialize(response);

                                sendDeviceToken();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (mOnResponseListener != null) {
                                mOnResponseListener.onError(new LPError(e));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (mOnResponseListener != null) {
                            mOnResponseListener.onError(new LPError(anError));
                        }
                    }
                });
    }

    public void logIn(Map<String, Object> params) {
        AndroidNetworking.post(API_LOGIN)
                .addBodyParameter(params)
                .setTag("login")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                UserManager.getInstance().initialize(response);

                                sendDeviceToken();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (mOnResponseListener != null) {
                                mOnResponseListener.onError(new LPError(e));
                            }
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (mOnResponseListener != null) {
                            mOnResponseListener.onError(new LPError(anError));
                        }
                    }
                });
    }

    public void changeProfile(Map<String, String> params) {
        AndroidNetworking.put(API_CHANGE_PROFILE)
                .addHeaders(AUTHORIZATION, UserManager.getInstance().getUser().getToken())
                .addBodyParameter(params)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                UserManager.getInstance().initialize(response);

                                if (mOnResponseListener != null) {
                                    mOnResponseListener.onContactServerSuccess();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (mOnResponseListener != null) {
                                mOnResponseListener.onError(new LPError(e));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (mOnResponseListener != null) {
                            mOnResponseListener.onError(new LPError(anError));
                        }
                    }
                });
    }

    private void sendDeviceToken() {

        User user = UserManager.getInstance().getUser();

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user.getId()));
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        params.put("os", "Android");
        params.put("is_production", "true");

        AndroidNetworking.post(API_PUT_DEVICE_TOKEN)
                .addHeaders(AUTHORIZATION, user.getToken())
                .addBodyParameter(params)
                .setTag("send_device_token")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("UserDS sendDeviceToken", response.toString());
                        if (mOnResponseListener != null) {
                            mOnResponseListener.onContactServerSuccess();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("UserDS DT Error", anError.getErrorBody());
                        if (mOnResponseListener != null) {
                            mOnResponseListener.onError(new LPError(anError));
                        }
                    }
                });
    }

    public void deleteDeviceToken(final OnResponseListener mOnResponseListener) {

        AndroidNetworking.post(API_LOGOUT)
                .addHeaders(AUTHORIZATION, UserManager.getInstance().getUser().getToken())
                .addBodyParameter(DEVICE_TOKEN, FirebaseInstanceId.getInstance().getToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("Response", String.valueOf(response.getBoolean("success")));
                            if (response.getBoolean("success")) {
                                if (mOnResponseListener != null) {
                                    mOnResponseListener.onContactServerSuccess();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (mOnResponseListener != null) {
                                mOnResponseListener.onError(new LPError(e));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (mOnResponseListener != null) {
                            mOnResponseListener.onError(new LPError(anError));
                        }
                    }
                });
    }

    public void uploadImage(File file) throws IOException {

        AndroidNetworking.upload(API_UPLOAD_IMAGE)
                .addHeaders("Authorization", UserManager.getInstance().getUser().getToken())
                .addMultipartFile("upload", file)
                .addMultipartParameter("model", "user")
                .setTag("upload_avatar")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject data = response.getJSONObject("data");
                                String url = data.getString("url");
                                UserManager.getInstance().getUser().getImage().setImgUrl(url);
                                if (mOnResponseListener != null) {
                                    mOnResponseListener.onContactServerSuccess();
                                }
                            }
                        } catch (JSONException e) {
                            if (mOnResponseListener != null) {
                                mOnResponseListener.onError(new LPError(e));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (mOnResponseListener != null) {
                            mOnResponseListener.onError(new LPError(anError));
                        }
                    }
                });
    }

    public void getUserFromAPI() {
        AndroidNetworking.post(API_CURRENT_USER)
                .addHeaders(AUTHORIZATION, UserManager.getInstance().getUser().getToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                UserManager.getInstance().initialize(response);

                                sendDeviceToken();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (mOnResponseListener != null) {
                                mOnResponseListener.onError(new LPError(e));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (mOnResponseListener != null) {
                            mOnResponseListener.onError(new LPError(anError));
                        }
                    }
                });
    }

    public interface OnResponseListener {
        void onContactServerSuccess();

        void onError(LPError error);
    }
}
