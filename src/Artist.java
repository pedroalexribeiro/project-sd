import java.io.Serializable;
import java.util.ArrayList;

public class Artist implements Serializable {
    public String name;
    public String details;
    public int id;

    public Artist(String name,String details, int id) {
        this.name = name;
        this.details = details;
        this.id = id;
    }

    public String toString(){
        return "Name: "+ name + ", details: "+details;
    }

    public void setID(int id){
        this.id=id;
    }
}