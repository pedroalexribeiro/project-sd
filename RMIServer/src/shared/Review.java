package shared;
import java.io.Serializable;

public class Review implements Serializable {
    private String username;
    private int album_id ;
    private String text;
    private int rating;
    private String date;

    public Review(String username,int album_id,String text,int rating,String date) {
        this.username=username;
        this.album_id=album_id;
        this.text=text;
        this.rating=rating;
        this.date=date;
    }

    public String getUsername() {
        return username;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    public String toString(){
        return username +"  Rated:"+ rating+" date:"+date+ "\n"+text;
    }
}
