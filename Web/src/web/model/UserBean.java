package web.model;

import rmiserver.Interface;
import shared.User;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class UserBean {
    private Interface server;

    public UserBean() {
        try {
            server = (Interface) LocateRegistry.getRegistry("192.168.1.12", 7000).lookup("Server");
        }
        catch(NotBoundException|RemoteException e) {
            e.printStackTrace();
        }
        return;
    }

    public User Login(String username,String password) throws RemoteException {
            return server.login(username,password);
    }
    public String Register(String username, String pass, String email, String name, Boolean edit) throws RemoteException {
        return server.register(username,pass,email,name,edit);
    }
    public void Logout(String username) throws RemoteException {
        server.logout(username);
        return;
    }

}
