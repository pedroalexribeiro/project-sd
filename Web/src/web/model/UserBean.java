package web.model;

import rmiserver.Interface;
import shared.User;
import shared.Notification;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

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
    }
    public ArrayList<User> searchUser(String username) throws RemoteException{
        return server.searchUsers(username);
    }
    public void sendNotification(Notification note,String username) throws RemoteException{
        server.sendNotifcation(note,username);
    }
    public ArrayList<Notification> getOfflineNotifications(String username) throws RemoteException{
        return server.getNotifications(username);
    }
    public void clearNotifications(String username) throws RemoteException{
        server.clearDatabaseNotifications(username);
    }

}
