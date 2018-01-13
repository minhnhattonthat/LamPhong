package com.lamphongstore.lamphong.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by HungNguyen on 3/23/17.
 */

public class PointInfo implements Serializable {
    private int point;
    private int next_level;
    private int need_point;
    private String next_level_type;
    private String member_type;

    public PointInfo(JSONObject pointInfo) throws JSONException {
        this.point = pointInfo.getInt("point");
        this.need_point = pointInfo.getInt("need_point");
        this.next_level = pointInfo.getInt("next_level");
        this.next_level_type = pointInfo.getString("next_level_type");
        this.member_type = pointInfo.getString("member_type");
    }

    public int getPoint() {
        return point;
    }

    public int getNextLevel() {
        return next_level;
    }

    public int getNeedPoint() {
        return need_point;
    }

    public String getNextLevelType() {
        return next_level_type;
    }

    public String getMemberType() {
        return member_type;
    }
}
