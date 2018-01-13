package com.lamphongstore.lamphong.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HungNguyen on 3/20/17.
 */

public class PromotionItem {
    private int id;
    private String title;
    private String date_to;
    private String description;
    private boolean isRead;


    public PromotionItem(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.title = jsonObject.getString("title");
        this.date_to = jsonObject.getString("date_to");
        this.description = jsonObject.getString("description");
        this.isRead = false;
    }

    private String FormatDate(String initDate) throws ParseException {
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat formatOutput = new SimpleDateFormat("dd/MM/yyyy");
        Date date = formatInput.parse(initDate);
        return formatOutput.format(date);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate_to() throws ParseException {
        return FormatDate(date_to);
    }

    public String getDescription() {
        return description;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
