package web.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    public int id;
    public String username;
    public String title;
    public ArrayList<Music> songs = new ArrayList<>();

    public Playlist(int id, String user,String title) {
        this.id = id;
        this.username = user;
        this.title = title;
    }


}