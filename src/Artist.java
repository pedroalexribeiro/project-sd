import java.io.Serializable;
import java.util.ArrayList;

public class Artist implements Serializable {
    public String name;
    public String details;
    public ArrayList<Music> songs = new ArrayList<Music>();

    public Artist(String name,String details) {
        this.name = name;
        this.details = details;
    }
}