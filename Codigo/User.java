import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public String username;
    public String password;
    public String email;
    public String name;
    public String personalInfo;// ?
    public Boolean editor;
    public Boolean online;

    public clientInterface cInterface;

    public ArrayList<Review> reviews = new ArrayList<>();
    public ArrayList<Playlist> playlists = new ArrayList<>();
    public ArrayList<Notification> notifications = new ArrayList<>();
    public ArrayList<Music> musicFiles = new ArrayList<>();

    public User(String user, String pass, String email, String name, Boolean edit) {
        this.username = user;
        this.password = pass;
        this.email = email;
        this.name = name;
        this.editor = edit;
        this.personalInfo = "";
        this.online = false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Boolean isEditor() {
        return editor;
    }

    public void setEditor(Boolean editor) {
        this.editor = editor;
    }

    public Boolean isOnline(){ return online; }

    public ArrayList<Notification> getNotifications(){return notifications;}

}