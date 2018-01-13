package com.lamphongstore.lamphong.data;


import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.lamphongstore.lamphong.ApiLamPhong;
import com.lamphongstore.lamphong.model.LPError;
import com.lamphongstore.lamphong.model.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bipug on 3/29/17.
 */





public class StoreDataStore {
    public interface OnResponseListener {
        void onSuccess(ArrayList<Store> storeList);

        void onError(LPError lpError);
    }

    public OnResponseListener mOnResponseListener;

    public void setOnResponseListener(OnResponseListener mOnResponseListener) {
        this.mOnResponseListener = mOnResponseListener;
    }
    public StoreDataStore() {
    }

    public void getDataFromServer(final OnResponseListener mOnResponseListener) {

        AndroidNetworking.get(ApiLamPhong.API_LIST_STORE)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            ArrayList<Store> storeList = null;
                            JSONArray storeDataArr = response.getJSONArray("data");
                            for (int i = 0; i < storeDataArr.length(); i++) {
                                JSONObject storeJsonObject = storeDataArr.getJSONObject(i);
                                if (storeList == null) storeList = new ArrayList<>();
                                Store newStore = new Store(storeJsonObject);
                                storeList.add(newStore);
                            }
                            if (mOnResponseListener != null) {
                                mOnResponseListener.onSuccess(storeList);
                            }
                        } catch (JSONException e) {
                            Log.e("StoreDataStore", e.getMessage());

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
}