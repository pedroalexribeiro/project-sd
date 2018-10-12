import java.rmi.*;

public interface Interface extends Remote {
    public String helloWorld() throws RemoteException;

    public User register(String username,String pass,String email,String name,Boolean edit) throws RemoteException;

    public User login(String username,String pass) throws RemoteException;

    public void subscribe(String username,clientInterface cInterface) throws RemoteException;

    public void sendNotifcation(Notification note,String username) throws RemoteException;

    public Boolean isAlive() throws RemoteException;
}
