package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import rest.DropBoxRestClient;
import shared.File;
import shared.User;
import web.model.UserBean;

import java.util.ArrayList;
import java.util.Map;


public class DropboxFile extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private ArrayList<File> files;
    private String username;
    private String other_username;
    private String other_drop_id;
    private String dropbox_id;
    private int music_id = 0;

    public ArrayList<File> getFiles() {
        return files;
    }

    public String execute() throws Exception{
        if(username != null && dropbox_id != null && music_id != 0){
            getUserBean().associateFile(username, dropbox_id, music_id);
            return SUCCESS;
        }else{
            return ERROR;
        }
    }

    public String shareFile() throws Exception{
        if(username != null && other_username != null && music_id != 0 && dropbox_id != null && other_drop_id != null){
            getUserBean().shareFile(other_username, username, music_id);
            getDropbox().shareFile(dropbox_id, other_drop_id, getUser().dropbox_token);
            return SUCCESS;
        }else{
            return ERROR;
        }
    }

    public String showOwnFiles() throws Exception{
        files = getUserBean().getOwnFiles(getUser().getUsername());
        if(files == null || files.isEmpty()){
            return NONE;
        }
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

    public String getOther_username() {
        return other_username;
    }

    public void setOther_username(String other_username) {
        this.other_username = other_username;
    }

    public String getOther_drop_id() {
        return other_drop_id;
    }

    public void setOther_drop_id(String other_drop_id) {
        this.other_drop_id = other_drop_id;
    }

    public UserBean getUserBean() {
        if(!session.containsKey("userBean"))
            this.setUserBean(new UserBean());
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean) {
        this.session.put("userBean", userBean);
    }
}
