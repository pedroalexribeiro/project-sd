import java.rmi.*;

public interface clientInterface extends Remote {

    public void liveNotification(Notification note) throws RemoteException;

}