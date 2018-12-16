package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import rest.DropBoxRestClient;
import shared.File;
import shared.User;

import java.util.ArrayList;
import java.util.Map;


public class Dropbox extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private ArrayList<File> files;
    private int musicId;

    public ArrayList<File> getFiles() {
        return files;
    }

    public String execute() throws Exception{
        files = getDropbox().listFiles(getUser().dropbox_token);
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

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }
}
