package com.lamphongstore.lamphong.utils;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lamphongstore.lamphong.data.UserManager;
import com.lamphongstore.lamphong.model.PointInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Norvia on 22/03/2017.
 */

public class LPFirebaseMessagingService extends FirebaseMessagingService {

    public static final String UPDATE_POINT = "update_point";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("Noti", remoteMessage.getData().toString());
        String data = remoteMessage.getData().get("data");
        try {
            JSONObject jsonDataValue = new JSONObject(data);
            Log.e("PointData", jsonDataValue.toString());
            PointInfo pointInfo = new PointInfo(jsonDataValue);
            UserManager.getInstance().getUser().setPointInfo(pointInfo);
            UserManager.getInstance().cacheUser();
            Log.e("PointNotifi", UserManager.getInstance().getUser().getPointInfo().getPoint() + "");
            Intent intent = new Intent(UPDATE_POINT);
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
