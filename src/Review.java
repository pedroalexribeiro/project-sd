import java.io.Serializable;

public class Review implements Serializable {
    public User user;
    public Album album ;
    public String text;
    public double avgRating;
    public String date;

    public Review(User user,Album album,String text,double avgRating,String date) {
        this.user=user;
        this.album=album;
        this.text=text;
        this.avgRating=avgRating;
        this.date=date;
    }
}
