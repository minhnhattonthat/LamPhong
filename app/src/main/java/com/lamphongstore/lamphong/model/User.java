package com.lamphongstore.lamphong.model;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Norvia on 18/03/2017.
 */
@IgnoreExtraProperties
public class User implements Serializable {
    private int id;
    private String fullname;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private String birthday;
    private String user_code;
    private String invitation_code;
    private String updated_at;
    private String member_type;
    private String token;
    private String role;
    private PointInfo point_info;
    private ImageResource image;

    public User(JSONObject response) throws JSONException {

        this.token = response.getString("token");
        JSONObject data = response.getJSONObject("data");
        this.id = data.getInt("id");
        this.fullname = data.getString("fullname");
        this.email = data.getString("email");
        this.phone = data.getString("phone");
        this.address = data.getString("address");
        this.gender = data.getString("gender");
        this.birthday = data.getString("birthday");
        this.user_code = data.getString("user_code");
        this.invitation_code = data.getString("invitation_code");
        this.updated_at = data.getString("updated_at");
        this.member_type = data.getString("member_type");
        this.role = data.getString("role");

        JSONObject pointInfo = data.getJSONObject("point_info");
        point_info = new PointInfo(pointInfo);

        JSONObject imageInfo = data.getJSONObject("image");
        image = new ImageResource(imageInfo);
    }

    private String formatBirthday(String birthday) {
        String year = birthday.substring(0, 4);
        String month = birthday.substring(5, 7);
        String day = birthday.substring(8, 10);

        return day + " - " + month + " - " + year;
    }

    public int getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return formatBirthday(this.birthday);
    }

    public String getUser_code() {
        return user_code;
    }

    public String getInvitationCode() {
        return invitation_code;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getMemberType() {
        return member_type;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public PointInfo getPointInfo() {
        return point_info;
    }

    public ImageResource getImage() {
        return image;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPointInfo(PointInfo point_info) {
        this.point_info = point_info;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setInvitationCode(String invitation_code) {
        this.invitation_code = invitation_code;
    }

    public void setImage(ImageResource image) {
        this.image = image;
    }

    public void setMemberType(String member_type) {
        this.member_type = member_type;
    }
}
