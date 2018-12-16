package web.action;

import shared.Notification;
import shared.User;
import web.model.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class Login extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;

    @Override
    public String execute() throws Exception{
        if(this.username != null && this.password != null && !username.equals("") && !password.equals("")) {
            User user = getUserBean().Login(username,password);
            if(user != null){
                ArrayList<Notification> notes = getUserBean().getOfflineNotifications(username);

                getUserBean().clearNotifications(username);
                session.put("offlineNotifications",notes);
                session.put("user", user);
                return SUCCESS;
            }
            addActionError("No user with those Credentials or user is already Online");
            return LOGIN;
        }
        addActionError("Username and Password can't be blanked");
        return LOGIN;
    }


    public void setUsername(String username) {
        this.username = username; // will you sanitize this input? maybe use a prepared statement?
    }

    public void setPassword(String password) {
        this.password = password; // what about this input?
    }


    public UserBean getUserBean() {
        if(!session.containsKey("userBean"))
            this.setUserBean(new UserBean());
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean) {
        this.session.put("userBean", userBean);
    }


    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
