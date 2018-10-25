import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    public User user;
    public String title;
    public ArrayList<Music> songs = new ArrayList<>();

    public Playlist(User user,String title) {
        this.user = user;
        this.title = title;
    }


}