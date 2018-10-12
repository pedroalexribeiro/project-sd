import java.io.Serializable;
import java.util.ArrayList;

public class Music implements Serializable {
    public String name;
    public String type;
    public double length;
    public Album album;
    public ArrayList<Artist> artists = new ArrayList<Artist>();

    public Music(String name,String type,double length,Album album) {
        this.name = name;
        this.type = type;
        this.length=length;
        this.album=album;
    }
}
