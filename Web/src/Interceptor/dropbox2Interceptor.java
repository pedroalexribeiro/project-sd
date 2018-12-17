package Interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.Interceptor;
import shared.User;
import web.action.Login;
import web.action.Register;

import java.util.Map;


public class dropbox2Interceptor implements Interceptor {
    private static final long serialVersionUID = 189237412378L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();
        User user = (User)session.get("user");
        if(user.dropbox_token != null) { // If user is trying to Enter EditorOnly pages
            return ActionSupport.NONE;
        }
        else
            return invocation.invoke();

    }

    @Override
    public void init() { }

    @Override
    public void destroy() { }
}