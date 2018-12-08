package shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    public String title;
    public double AvgRating;
    public String releaseDate;
    public String description;
    public String artist;
    public int id;
    public Album(String name,String rel,String description,String artist,int id) {
        this.title = name;
        this.releaseDate=rel;
        this.description=description;
        this.artist = artist;
        this.AvgRating = 0;
        this.id=id;
    }



    public String getTitle() {
        return title;
    }

    public double getAvgRating() {
        return AvgRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public String getArtist() {
        return artist;
    }

    public int getId() {
        return id;
    }

    public String toString(){
        return title+" ,"+releaseDate +" ,"+description+" ,"+artist;
    }
}