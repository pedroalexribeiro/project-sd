package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import rest.DropBoxRestClient;
import shared.File;
import shared.User;

import java.util.ArrayList;
import java.util.Map;


public class DropboxFile extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private ArrayList<File> files;
    private String username;
    private String dropbox_id;
    private int music_id;

    public ArrayList<File> getFiles() {
        return files;
    }

    public String execute() throws Exception{
        //files = getDropbox().listFiles(getUser().dropbox_token);
        getDropbox().downloadFile(dropbox_id, getUser().dropbox_token);
        return SUCCESS;
    }

    public DropBoxRestClient getDropbox() {
        if(!session.containsKey("dropbox"))
            this.setDropbox(new DropBoxRestClient("http://localhost:8080/Web/add-dropbox"));
        return (DropBoxRestClient) session.get("dropbox");
    }

    public void setDropbox(DropBoxRestClient dropbox) {
        this.session.put("dropbox", dropbox);
    }

    public User getUser(){
        return (User) session.get("user");
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDropbox_id() {
        return dropbox_id;
    }

    public void setDropbox_id(String dropbox_id) {
        this.dropbox_id = dropbox_id;
    }

    public int getMusic_id() {
        return music_id;
    }

    public void setMusic_id(int music_id) {
        this.music_id = music_id;
    }
}
