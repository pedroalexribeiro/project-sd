import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    public String username;
    public String title;
    public ArrayList<Music> songs = new ArrayList<>();

    public Playlist(String user,String title) {
        this.username = user;
        this.title = title;
    }


}