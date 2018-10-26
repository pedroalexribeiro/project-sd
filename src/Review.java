import java.io.Serializable;

public class Review implements Serializable {
    public String username;
    public int album_id ;
    public String text;
    public int score;
    public String date;

    public Review(String username,int album_id,String text,int score,String date) {
        this.username=username;
        this.album_id=album_id;
        this.text=text;
        this.score=score;
        this.date=date;
    }
}
