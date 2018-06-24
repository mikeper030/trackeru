package models;

/**
 * Created by mike on 24 Dec 2017.
 */

public class LocationModel {
  private double lat;
    private double longt;
    private String username;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

      public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public double getLongt() {
        return longt;
    }

    public void setLongt(double longt) {
        this.longt = longt;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
  public LocationModel(){

  }
}
