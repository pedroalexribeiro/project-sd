import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.concurrent.TimeUnit;

public class RMIServer extends UnicastRemoteObject implements Interface {

    private static final long serialVersionUID = 1L;

    private static User tempUser;

    public RMIServer() throws RemoteException {
        super();
    }

    public String helloWorld() throws RemoteException {
        System.out.println("Hello World");
        return "Went to the dark side and survived!";
    }

    public User login(String username,String pass) throws RemoteException {
        /*Goes to database and checks if user exists and returns it
        *
        * If not ->return null*/
        System.out.println("User: "+username+" logged in");
        tempUser = new User(username,pass,"email","name",true);
        return tempUser;
    }

    public User register(String username,String pass,String email,String name,Boolean edit){
        /*Goes to database and checks if user with that username exists
        * if not <- Creates it
        * else <- return null*/
        tempUser = new User(username,pass,"email","name",true);
        return tempUser;

    }

    public void subscribe(String username,clientInterface cInterface){
        /*Searches user on database and updates user.clientInterface Option so it saves the interface*/
        if(tempUser.username.equals(username)){
            tempUser.cInterface = cInterface;
        }
    }

    public void sendNotifcation(Notification note,String username) throws RemoteException{
        /*Fetch cInterface from Database of username*/

        System.out.println(tempUser.username+ " : " + username);
        if(tempUser.username.equalsIgnoreCase(username)){
            tempUser.cInterface.liveNotification(note);
        }
    }

    public Boolean isAlive() throws RemoteException {
        return true;
    }

    public static void main(String args[]) {
        try { // First checks if registry is created
            Registry r = LocateRegistry.createRegistry(7000);

        } catch (Exception e) {
            System.out.println("Already Created");
        }

        try { // Second checks if a server is UP
            Interface i = (Interface) LocateRegistry.getRegistry(7000).lookup("Server");
            System.out.println("Server already up");
            // Else --> isAlive();
            int count = 1;
            while (count != 0) {
                System.out.println("While");
                try {
                    if (i.isAlive()) {// Keep checking
                        TimeUnit.SECONDS.sleep(1); // sleep for a bit
                    }
                } catch (InterruptedException ie) {
                    System.out.println(ie);
                } catch (RemoteException re2) {
                    expired(i, count); // <-- Call when main server dies
                    count = 0;
                }
            }
        } catch (NotBoundException e) { // If NOT --> start the server
            startServer();
        } catch (RemoteException re) {
            System.out.println(re);
        }
    }

    public static int expired(Interface i, int count) {
        try {
            System.out.println(count);
            if (count == 5) { // Creates a new Server
                Registry r = LocateRegistry.createRegistry(7000);
                startServer();
                return 0;
            }
            if (!i.isAlive()) {} // If its still not existing, keep increasing counts

        } catch (RemoteException r) {
            System.out.println("count++");
            count++;
            try {
                TimeUnit.SECONDS.sleep(1); // sleep for a bit
            } catch (InterruptedException ie) {
                System.out.println(ie);
            }
            expired(i, count); // Recall function until count==5
        }
        return count;
    }

    public static void startServer() {
        try {
            Registry r = LocateRegistry.getRegistry(7000);
            RMIServer rs = new RMIServer();
            r.rebind("Server", rs);
            System.out.println("Server Ready");
        } catch (RemoteException re) {
            System.out.println("Exception in RMIServer.main" + re);
        }
    }
}

/*
 *
 * Callback <- capacidade do server invocar metodos no cliente
 *
 */
