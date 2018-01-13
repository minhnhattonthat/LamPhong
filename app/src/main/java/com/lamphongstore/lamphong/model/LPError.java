package com.lamphongstore.lamphong.model;

import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

/**
 * Created by Norvia on 29/03/2017.
 */

public class LPError implements Serializable {
    private String code;
    private String detail;
    private String msg;
    private String show;

    public LPError(ANError anError) {
        if (anError.getErrorCode() != 0) {
            try {
                JSONObject response = new JSONObject(anError.getErrorBody());
                JSONObject error = response.getJSONObject("error");
                this.code = error.getString("code");
                this.detail = error.getString("detail");
                this.msg = error.getString("msg");
                this.show = error.getString("show");
            } catch (JSONException e) {
                e.printStackTrace();
                this.code = String.valueOf(anError.getErrorCode());
                this.detail = anError.getMessage();
                this.msg = anError.getErrorDetail();
                this.show = anError.getErrorDetail();
            }
        } else {
            this.code = String.valueOf(anError.getErrorCode());
            this.detail = anError.getMessage();
            this.msg = anError.getErrorDetail();
            this.show = anError.getErrorDetail();
        }
    }

    public LPError(JSONException e) {
        this.code = e.getClass().toString();
        this.detail = e.getCause().toString();
        this.msg = e.getMessage();
        this.show = e.getMessage();
    }

    public LPError(ParseException e) {
        this.code = e.getClass().toString();
        this.detail = e.getCause().toString();
        this.msg = e.getMessage();
        this.show = e.getMessage();
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }

    public String getMsg() {
        return msg;
    }

    public String getShow() {
        return show;
    }
}
