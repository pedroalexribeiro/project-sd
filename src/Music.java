import java.io.Serializable;
import java.util.ArrayList;

public class Music implements Serializable {
    public String name;
    public String type;
    public int length;
    public int album_id;
    public ArrayList<Artist> artists = new ArrayList<Artist>();

    public Music(String name,String type,int length,int album_id) {
        this.name = name;
        this.type = type;
        this.length=length;
        this.album_id=album_id;
    }
}
