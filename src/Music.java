import java.io.Serializable;

public class Music implements Serializable {
    public String name;
    public String type;
    public int length;
    public int album_id;
    public int id;

    public Music(String name,String type,int length,int album_id, int id) {
        this.name = name;
        this.type = type;
        this.length=length;
        this.album_id=album_id;
        this.id = id;
    }

    public String toString() {
        return name+" ,"+type+" ,"+ length;
    }
}
