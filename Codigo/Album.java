import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    public String title;
    public double AvgRating;
    public String releaseDate;
    public String description;
    public String artist;
    public ArrayList<Music> songs = new ArrayList<>();
    public ArrayList<Review> reviews = new ArrayList<>();
    public ArrayList<User> editors = new ArrayList<>();

    public Album(String name,String rel,String description,String artist) {
        this.title = name;
        this.releaseDate=rel;
        this.description=description;
        this.artist = artist;
        this.AvgRating = 0;

    }
}