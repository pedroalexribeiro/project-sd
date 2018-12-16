package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.Notification;
import web.model.UserBean;

import java.util.Map;

public class MakeEditor extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String user;


    @Override
    public String execute() throws Exception{
        //WebSocket.getUserSessions().get(user).getBasicRemote().sendText("asdasd");
        Notification note = new Notification(this.user,"You are now an Editor");
        getUserBean().sendNotification(note,user);
        addActionMessage("This User is Now an Editor");
        return SUCCESS;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
