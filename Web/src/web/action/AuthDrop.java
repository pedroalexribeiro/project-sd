package web.action;

import rest.DropBoxRestClient;
import shared.User;
import web.model.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.Map;

public class AuthDrop extends ActionSupport implements SessionAware {
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
            getUserBean().addDropbox(getUser().username, client_id, token);
            return SUCCESS;
        }
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DropBoxRestClient getDropbox() {
        if(!session.containsKey("dropbox"))
            this.setDropbox(new DropBoxRestClient("http://localhost:8080/Web/add-dropbox"));
        return (DropBoxRestClient) session.get("dropbox");
    }

    public void setDropbox(DropBoxRestClient dropbox) {
        this.session.put("dropbox", dropbox);
    }

    public UserBean getUserBean() {
        if(!session.containsKey("userBean"))
            this.setUserBean(new UserBean());
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean) {
        this.session.put("userBean", userBean);
    }

    public User getUser() {
        return (User) session.get("user");
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
