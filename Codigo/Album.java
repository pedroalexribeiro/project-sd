import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    public String name;
    public double AvgRating;
    public String releaseDate;
    public String composer;
    public String history;
    public ArrayList<Music> songs = new ArrayList<>();
    public ArrayList<Review> reviews = new ArrayList<>();
    public ArrayList<User> editors = new ArrayList<>();

    public Album(String name,String rel,String comp,String hist) {
        this.name = name;
        this.releaseDate=rel;
        this.composer=comp;
        this.history=hist;
        this.AvgRating = 0;

    }
}