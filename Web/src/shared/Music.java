package shared;

import java.io.Serializable;

public class Music implements Serializable {
    static final long serialVersionUID = 42L;
    public String name;
    public String type;
    public int length;
    public int album_id;
    public int id;
    public String lyrics;

    public Music(String name,String type,int length,String lyrics,int album_id, int id) {
        this.name = name;
        this.type = type;
        this.length=length;
        this.album_id=album_id;
        this.id = id;
        this.lyrics=lyrics;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String toString() {
        return name+" ,"+type+" ,"+ length+" ,"+lyrics;
    }
}
