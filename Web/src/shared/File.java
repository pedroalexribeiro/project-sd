package shared;

import java.io.Serializable;

public class File implements Serializable {
    static final long serialVersionUID = 42L;
    public String name;
    public String id;
    public String username;
    public int musicId;
    public String tmpLink;

    public File(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public String getTmpLink() {
        return tmpLink;
    }

    public void setTmpLink(String tmpLink) {
        this.tmpLink = tmpLink;
    }
}
