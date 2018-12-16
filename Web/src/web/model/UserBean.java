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
            server = (Interface) LocateRegistry.getRegistry("192.168.1.8", 7000).lookup("Server");
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

    public void addDropbox(String username, String dropbox_id, String dropbox_token) throws RemoteException{
        String teste = server.addDropbox(username, dropbox_id, dropbox_token);
        System.out.println(teste);
    }

    public void associateFile(String username, String dropbox_id, int music_id) throws RemoteException {
        server.addFile(dropbox_id, username, music_id);
    }

    public User loginDrop(String client_id) throws RemoteException {
        return server.loginDrop(client_id);
    }

}
