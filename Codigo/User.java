import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public String username;
    public String password;
    public String email;
    public String name;
    public String personalInfo;// ?
    public Boolean editor;

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

    public Boolean getEditor() {
        return editor;
    }

    public void setEditor(Boolean editor) {
        this.editor = editor;
    }

}