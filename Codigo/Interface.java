import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Interface extends Remote {
    String helloWorld() throws RemoteException;

    User register(String username,String pass,String email,String name,Boolean edit) throws RemoteException;

    User login(String username,String pass) throws RemoteException;

    //Saves clientInterface/onlineStatus
    void subscribe(String username,clientInterface cInterface) throws RemoteException;

    //send/save notification
    void sendNotifcation(Notification note,String username,Boolean edit) throws RemoteException;

    //Deletes notifications on user line
    void clearDatabaseNotifications(String username) throws RemoteException;


    String playlistMethods(String method,String word,String music,String username) throws RemoteException;

    //Search Method
    ArrayList<String> search(String word,String whereSearch) throws RemoteException;


    Boolean isAlive() throws RemoteException;

    String askIP() throws RemoteException;
}
