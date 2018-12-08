package shared;
import java.io.Serializable;
import java.util.ArrayList;

public class Artist implements Serializable {
    static final long serialVersionUID = 42L;
    public String name;
    public String details;
    public int id;
    public boolean solo;

    public Artist(String name,String details, boolean solo, int id) {
        this.name = name;
        this.details = details;
        this.solo = solo;
        this.id = id;
    }

    public String toString(){
        return "Name: "+ name + " details: "+details;
    }

    public void setID(int id){
        this.id=id;
    }
}