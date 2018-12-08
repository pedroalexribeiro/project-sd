package shared;
import java.io.Serializable;

public class Notification implements Serializable {
    public String user;
    public String text;
    public Boolean delivered;

    public Notification(String user,String text) {
        this.user = user;
        this.text= text;
        this.delivered = false;
    }
}