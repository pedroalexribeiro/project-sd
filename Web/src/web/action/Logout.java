package web.action;

import shared.User;
import web.model.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class Logout extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() throws Exception{
        User user = (User) session.get("user");
        getUserBean().Logout(user.username);
        session.clear();
        addActionMessage("Logout Successful");
        return SUCCESS;
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
