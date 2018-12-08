package web.action;

import web.model.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;

public class Login extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;

    @Override
    public String execute() throws Exception{
        if(this.username != null && this.password != null && !username.equals("") && !password.equals("")) {



            /*if(getUserBean().Login(username,password) != null){
                session.put("username",this.username);
                session.put("password",this.password);
                session.put("loggedIn", true);
                return SUCCESS;
            }*/

            System.out.println(getUserBean().helloWorld());
            return LOGIN;
        }
        else
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
