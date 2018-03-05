package com.homeless.Logic;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by vladi on 2/25/2018.
 */
@IgnoreExtraProperties
public class User {
    @Exclude
    private String uId;
    private String name;
    private String eMail;
    private String phone;
    private String password;
    @Exclude
    private ArrayList <Review> reviews;

    public User (){
        this.reviews = new ArrayList<>();
    }
    public User(String uId, String name, String phone, String password,String eMail) {
        this.uId = uId;
        this.eMail = eMail;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.reviews = new ArrayList<>();
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    @Exclude
    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}
