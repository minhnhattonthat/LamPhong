package com.lamphongstore.lamphong.data;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.lamphongstore.lamphong.ApiLamPhong.API_LIST_ADS;

/**
 * Created by HungNguyen on 3/29/17.
 */

public class AdsDataStore {

    private AdsResultListener adsResultListener;

    public AdsDataStore() {
    }

    public void setAdsResultListener(AdsResultListener listener) {
        this.adsResultListener = listener;
    }

    public void getListAds() {
        AndroidNetworking.get(API_LIST_ADS)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray arrayAds = response.getJSONArray("data");

                                UserManager.getInstance().setAdsList(arrayAds);

                                if (adsResultListener != null) {
                                    adsResultListener.onSuccess();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (adsResultListener != null) {
                            adsResultListener.onError();
                        }
                    }
                });
    }

    public interface AdsResultListener {
        void onSuccess();

        void onError();
    }
}
