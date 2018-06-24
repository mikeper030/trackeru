package models;

import java.util.ArrayList;

/**
 * Created by mike on 10 Dec 2017.
 */

public class usercircleModel {

    //model class for each circle created by user i.e listview 1
    private String Name;
    private String imgUrl;
    private int imgRotation;

    public int getImgRotation() {
        return imgRotation;
    }

    public void setImgRotation(int imgRotation) {
        this.imgRotation = imgRotation;
    }

    private String key;
      private ArrayList <User> User;


    public ArrayList<User> getUser() {
        return User;
    }

    public void setUser(ArrayList<User> User) {
        this.User = User;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String userName) {
        this.Name = userName;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {

        return Name;
    }

    public String getImgUrl() {
        return imgUrl;
    }



    public usercircleModel() {



    }

}
