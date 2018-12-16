package shared;

import rmiclient.clientInterface;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    static final long serialVersionUID = 42L;
    public String username;
    public String password;
    public String email;
    public String name;
    public String personalInfo;
    public Boolean editor;
    public Boolean online;
    public String dropbox_token;
    public clientInterface cInterface;


    public ArrayList<Notification> notifications = new ArrayList<>();

    public User(String user, String pass, String email, String name, Boolean edit) {
        this.username = user;
        this.password = pass;
        this.email = email;
        this.name = name;
        this.editor = edit;
        this.personalInfo = "";
        this.online = false;
    }

    public User(String user, String pass, String email, String name, Boolean edit, String dropbox_token) {
        this.username = user;
        this.password = pass;
        this.email = email;
        this.name = name;
        this.editor = edit;
        this.personalInfo = "";
        this.online = false;
        this.dropbox_token = dropbox_token;
    }


    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public Boolean getEditor() {
        return editor;
    }

    public Boolean isEditor() {
        return editor;
    }

    public void setEditor(Boolean editor) {
        this.editor = editor;
    }

    public Boolean isOnline(){ return online; }

    public ArrayList<Notification> getNotifications(){return notifications;}

}