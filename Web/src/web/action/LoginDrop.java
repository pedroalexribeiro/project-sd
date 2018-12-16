package web.action;

import rest.DropBoxRestClient;
import shared.User;
import web.model.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;

public class LoginDrop extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String code = "";
    private String url = null;

    @Override
    public String execute() throws Exception{
        if(code.equals("")){
            url = getDropbox().getUrl();
            return "redirect";
        }else {
            String token = getDropbox().getToken(code);
            String client_id = getDropbox().getClientId(token);
            User user = getUserBean().loginDrop(client_id);
            if(user != null){
                session.put("user", user);
                return SUCCESS;
            }
            return LOGIN;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DropBoxRestClient getDropbox() {
        return new DropBoxRestClient("http://localhost:8080/Web/login-dropbox");
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


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
