import java.rmi.*;
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
    void clearDatabaseNotifications(User user) throws RemoteException;


    String playlistMethods(String method,String word,String music,User user) throws RemoteException;

    //Search Method
    ArrayList<String> search(String word,String whereSearch) throws RemoteException;

    //Retrieves information about 1 Music/Artist/Album
    ArrayList<String> retrieveInformation(String whereSearch,String option) throws RemoteException;


    Boolean isAlive() throws RemoteException;
}
