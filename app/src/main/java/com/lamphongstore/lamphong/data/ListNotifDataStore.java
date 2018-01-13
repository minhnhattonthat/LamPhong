package com.lamphongstore.lamphong.data;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.lamphongstore.lamphong.model.LPError;
import com.lamphongstore.lamphong.model.PromotionItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.lamphongstore.lamphong.ApiLamPhong.API_DEL_NOTIFICATION;
import static com.lamphongstore.lamphong.ApiLamPhong.API_LIST_NOTIFICATION;
import static com.lamphongstore.lamphong.ApiLamPhong.AUTHORIZATION;

/**
 * Created by HungNguyen on 4/3/17.
 */

public class ListNotifDataStore {

    private static final ListNotifDataStore instance = new ListNotifDataStore();
    private ListNotifListener listNotifListener;

    public ListNotifDataStore() {

    }

    public static ListNotifDataStore getInstance() {
        return instance;
    }

    public void setListNotifListener(ListNotifListener listener) {
        this.listNotifListener = listener;
    }

    public void getListNotifs() {
        AndroidNetworking.get(API_LIST_NOTIFICATION)
                .addHeaders(AUTHORIZATION, UserManager.getInstance().getUser().getToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject data = response.getJSONObject("data");

                                JSONArray notifications = data.getJSONArray("notifications");

                                UserManager.getInstance().setNotificationsList(notifications);

                                JSONObject promotion = data.getJSONObject("promotion");
                                PromotionItem promotionItem = new PromotionItem(promotion);
                                UserManager.getInstance().setPromotionItem(promotionItem);

                                if (listNotifListener != null) {
                                    listNotifListener.onSuccess();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (listNotifListener != null) {
                                listNotifListener.onError();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (listNotifListener != null) {
                            listNotifListener.onError();
                        }
                    }
                });
    }

    public void deleteNotiFromList(final int position, final CallBackDelNoti callBackDelNoti) {
        int notificationId = UserManager.getInstance().getNotificationsList().get(position).getId();
        AndroidNetworking.delete(API_DEL_NOTIFICATION + String.valueOf(notificationId))
                .addHeaders(AUTHORIZATION, UserManager.getInstance().getUser().getToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                UserManager.getInstance().deleteNotification(position);
                                if (callBackDelNoti != null) {
                                    callBackDelNoti.onSuccess();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (callBackDelNoti != null) {
                            callBackDelNoti.onError(new LPError(anError));
                        }
                    }
                });
    }

    public interface ListNotifListener {
        void onSuccess();

        void onError();
    }
}
