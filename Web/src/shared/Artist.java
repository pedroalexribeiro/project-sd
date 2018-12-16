package shared;

import java.io.Serializable;

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

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public int getId() {
        return id;
    }

    public String toString(){
        return "Name: "+ name + " details: "+details;
    }
}