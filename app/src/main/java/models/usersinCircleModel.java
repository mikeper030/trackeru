package models;

import java.io.Serializable;

/**
 * Created by mike on 13 Dec 2017.
 */

public class usersinCircleModel {

    //model class for friends in circle i.e listview 2

    private String username;
    private String url;
    private String status;

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return url;
    }

    public void setPicture(String picture) {
        this.url = picture;
    }

    public usersinCircleModel() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}