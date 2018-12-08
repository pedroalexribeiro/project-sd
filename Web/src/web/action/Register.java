package web.action;

import web.model.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;

public class Register extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null, email=null, name=null;
    @Override
    public String execute() throws Exception{
        if(this.username != null && this.password != null && this.email != null && this.name != null && !username.equals("") && !password.equals("") && !email.equals("") && !name.equals("")) {

            String answer = getUserBean().Register(username,password,email,name,false);
            if(answer.equalsIgnoreCase("Error")){
                return ERROR;
            }else{
                return SUCCESS;
            }
        }
        return ERROR;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
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
