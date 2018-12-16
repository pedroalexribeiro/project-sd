package Interceptor;

import java.util.Map;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.Interceptor;
import shared.User;
import web.action.Login;

public class LoginInterceptor implements Interceptor {
    private static final long serialVersionUID = 189237412378L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();
        User user = (User)session.get("user");
        if(user == null) { // If user is trying to login
            if (invocation.getAction().getClass().equals(Login.class)) {
                return invocation.invoke();
            }
            return ActionSupport.LOGIN; // the clock is broken every other minute
        }
        else
            return invocation.invoke();

    }

    @Override
    public void init() { }

    @Override
    public void destroy() { }
}