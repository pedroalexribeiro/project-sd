import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;
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

    public User login(String username, String pass) throws RemoteException {
        /*
         * Goes to database and checks if user exists and returns it
         *
         * If not ->return null
         */
        System.out.println("User: " + username + " logged in");
        tempUser = new User(username, pass, "email", "name", true);
        return tempUser;
    }

    public User register(String username, String pass, String email, String name, Boolean edit) {
        /*
         * Goes to database and checks if user with that username exists if not <-
         * Creates it else <- return null
         */
        tempUser = new User(username, pass, "email", "name", true);
        return tempUser;

    }

    public void subscribe(String username, clientInterface cInterface) {
        /*
         * Searches user on database and updates user.clientInterface Option so it saves
         * the interface
         */
        if (tempUser.username.equals(username)) {
            tempUser.cInterface = cInterface;
            tempUser.online = true;
        }
    }

    public void sendNotifcation(Notification note, String username, Boolean edit) throws RemoteException {
        /* Fetch cInterface from Database using username */

        System.out.println(tempUser.username + " : " + username);
        if (tempUser.username.equalsIgnoreCase(username)) {
            if (edit) {
                tempUser.setEditor(true);
            }

            if (tempUser.isOnline()) {
                tempUser.cInterface.liveNotification(note);
            } else {
                /*
                 * Enviar para databse para meter la note para user receber when he gets online
                 */
            }
        } else {
            System.out.println("User doesnt exist");
        }
    }

    public void clearDatabaseNotifications(User user) throws RemoteException {
        /* Ir a base dados limpar informacao de notificacoes no user */
    }

    public String playlistMethods(String method, String playlist, String music, User user) {
        switch (method) {
        case "create":
            /*
             * title = playlist user = user Go to Database and create Line
             */
            return "Playlist Created!";
        case "delete":
            /*
             * title = playlist user = user Go to database and delete Line What happens if
             * it doesnt exist?
             */
            return "Playlist " + playlist + "deleted";
        case "add":
            /*
             * title = playlist user = user go to database and fetch existing playlist <----
             * return "no existing playlist with that name" After checking if playlist
             * exists, check if music with that name exists <-- return
             * "That music already exists on that playlist"
             */
            return "Added " + music + " to " + playlist;
        case "remove":
            /*
             * title = playlist user = user go to database and fetch existing playlist <----
             * return "no existing playlist with that name" After checking if playlist
             * exists, check if music with that name exists <-- return
             * "That music doesnt exist on that playlist"
             */
            return "Deleted " + music + " from " + playlist;
        default:
            return "Method Does Not exist!";
        }
    }

    public ArrayList<String> search(String word, String whereSearch) {

        ArrayList<String> output = new ArrayList<>();

        switch (whereSearch.toLowerCase()) {
        case "album":
            /*
             * Search Database on table Album for any parameter equal to WORD Return them
             * all to UDP->Object convertion Return them here and fill the Arraylist<String>
             * of options
             */
            Album a1 = new Album("Album1", "20/04/1998", "composer1", "History1");
            Album a2 = new Album("Album2", "20/04/1998", "composer3", "History2");
            Album a3 = new Album("Album3", "20/04/1998", "composer2", "History3");

            output.add(a1.name + " " + a1.composer + " " + a1.releaseDate);
            output.add(a2.name + " " + a2.composer + " " + a2.releaseDate);
            output.add(a3.name + " " + a3.composer + " " + a3.releaseDate);

            break;
        case "artist":
            break;
        case "music":
            break;
        case "user":
            break;
        }

        return output;
    }

    public ArrayList<String> retrieveInformation(String whereSearch, String option) {
        /*
         * Go to data base and look for line with exact info on Option and retrieve them
         * all
         */

        return new ArrayList<>();
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
            if (i.isAlive()) {} // If its still not existing, keep increasing counts

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
