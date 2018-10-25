import java.rmi.Remote;
import java.rmi.RemoteException;

public interface clientInterface extends Remote {

    public void liveNotification(Notification note) throws RemoteException;

}