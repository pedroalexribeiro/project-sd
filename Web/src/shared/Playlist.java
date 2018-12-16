package shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    static final long serialVersionUID = 42L;
    public int id;
    public String username;
    public String title;
    public ArrayList<Music> songs = new ArrayList<>();

    public Playlist(int id, String user,String title) {
        this.id = id;
        this.username = user;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }
}