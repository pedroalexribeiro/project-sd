package rmiclient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface clientInterface extends Remote {

    void liveNotification(String note) throws RemoteException;

}