package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.Music;
import shared.User;
import web.model.MusicBean;

import java.util.Map;

public class DisplayUser extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username;
    private String email;
    private String name;
    private boolean editor;
    private String personalInfo;
    private String dropbox_id;


    @Override
    public String execute() throws Exception{
        User u = new User(this.username,"password",this.email,this.name,this.editor, "", dropbox_id);
        u.setPersonalInfo(this.personalInfo);
        session.put("searchUser",u);
        return SUCCESS;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isEditor() {
        return editor;
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }

    public String getDropbox_id() {
        return dropbox_id;
    }

    public void setDropbox_id(String dropbox_id) {
        this.dropbox_id = dropbox_id;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
