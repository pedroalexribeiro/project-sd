import java.rmi.Remote;
import java.rmi.RemoteException;

public interface clientInterface extends Remote {

    void liveNotification(Notification note) throws RemoteException;

}