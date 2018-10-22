import java.io.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.sql.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class RMIServer extends UnicastRemoteObject implements Interface {

    private static final long serialVersionUID = 1L;

    private static CopyOnWriteArrayList<User> onlineUsers = new CopyOnWriteArrayList<>();

    private static User tempUser;

    private static String MULTICAST_ADDRESS = "224.1.1.1";
    private static int MULTICAST_PORT = 4100;
    private static int RMI_PORT = 4000;
    private MulticastSocket senderSocket = null;
    private MulticastSocket receiverSocket = null;

    public RMIServer() throws RemoteException {
        super();
        try {
            this.receiverSocket = new MulticastSocket(RMI_PORT);  // create socket and bind it
            this.senderSocket = new MulticastSocket(); // create socket and doesn't bind it
        } catch (IOException e) {
            e.printStackTrace();
            this.receiverSocket.close();
            this.senderSocket.close();
        }
    }

    public String sendMulticast(String message){
        // Send it to multicast servers
        try {
            byte[] sendBuffer = message.getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, group, MULTICAST_PORT);
            this.senderSocket.send(packet);

            // Waits for multicast servers to respond
            byte[] receiveBuffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            this.receiverSocket.receive(request);
            return (new String(request.getData(), 0, request.getLength()));
        }catch(UnknownHostException ue){
            System.out.println(ue);
            return "error";
        }catch(IOException e){
            System.out.println(e);
            return "error";
        }
    }

    public String helloWorld() throws RemoteException {
        return sendMulticast("Something");
    }

    public User login(String username, String pass) throws RemoteException {
        for(User temp : onlineUsers){
            if (temp.username.equalsIgnoreCase(username)){
                System.out.println("The user "+ temp.username +" is already Online");
                return null;
            }
        }


        String t = "search | user ; username | "+username+" ; password | "+pass+" ;";
        //Send through Multicast

        System.out.println("User: " + username + " logged in");
        tempUser = new User(username, pass, "email", "name", true);
        onlineUsers.add(tempUser);
        writeFile();
        return tempUser;
    }

    public User register(String username, String pass, String email, String name, Boolean edit) {
        String t = "create | user ; username | "+username+" ; password | "+pass+" ; email | "+email+" ; name | "+name+" ; edit | "+edit+" ;";

        //Multicast udp.menu(str) and check result..
        // if "Null" -> User already Exists
        // Else -> Sucess!!

        tempUser = new User(username, pass, "email", "name", true);
        return tempUser;

    }

    public void subscribe(String username, clientInterface cInterface) {
        //Function that connects user with its interface
        for(User temp : onlineUsers){
            if (temp.username.equalsIgnoreCase(username)){
                temp.cInterface = cInterface;
            }
        }
    }

    public void sendNotifcation(Notification note, String username, Boolean edit) throws RemoteException {
        if(note.text.equalsIgnoreCase("Editor")){ //Change this
            for(User temp : onlineUsers){ // Is online?
                if (temp.username.equalsIgnoreCase(username)){
                    temp.cInterface.liveNotification(note);
                }
            }

            String t = "create | notification ; text | "+note.text+" ; username | "+username+" ;";
            //Multicast it to database and save it for when he gets online!
        }
    }

    public void clearDatabaseNotifications(String username) throws RemoteException {
        /* Ir a base dados limpar informacao de notificacoes no user */

        String t = "delete | notification ; username | "+username+ " ;";

    }

    public String playlistMethods(String method, String title, String music, String username) {
        String t;
        switch (method) {
        case "create":
            /*
             * title = playlist user = user Go to Database and create Line
             */
             t = "create | playlist ; title | "+title+" ; username | "+username+" ;";
            return "Playlist Created!";
        case "delete":
            /*
             * title = playlist user = user Go to database and delete Line What happens if
             * it doesnt exist?
             */

             t = "delete | playlist ; title | "+title+" ;";

            return "Playlist deleted";
        case "add":

            t = "search | playlist ; title | "+title+" ;";
            //playlistID = playlist.ID
            //if null return "error"

            t = "search | music ; name | "+music+" ;";
            //musicID = music.ID
            //if null return "error"

            //t = "create | playlist_music ; title | "+playlistID+" ; music | "+musicID+" ;";

            /*
            * Go to database and add
            * */
            return "Added " + music + " to " + title;
        case "remove":


            t = "search | playlist ; title | "+title+" ;";
            //playlistID = playlist.ID
            //if null return "error"

            t = "search | music ; name | "+music+" ;";
            //musicID = music.ID
            //if null return "error"

            //t = "delete | playlist_music ; title | "+playlistID+" ; music | "+musicID+" ;";

            /*
             * Go to database and SWITCH title and music with respective ID and delete that song
             * */
            return "Deleted " + music + " from "+title;
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

    public Boolean isAlive() throws RemoteException {
        return true;
    }

    public void writeFile(){
        try {
            FileOutputStream fileOut = new FileOutputStream("onlineUsers.ser");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(onlineUsers);
            objectOut.close();
            fileOut.close();
            System.out.println("The Object  was succesfully written to a file");
            } catch (Exception e) {
            System.out.println("Exception on writeFile" + e);
        }
    }

    public static void readFile(){
        try {
            FileInputStream fileIn = new FileInputStream("onlineUsers.ser");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            onlineUsers = (CopyOnWriteArrayList<User>) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            System.out.println("The Object  was succesfully read to a file");

            System.out.println(onlineUsers);
        } catch (Exception e) {
            System.out.println("Exception on readFile");
        }
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
            r.rebind("//192.168.1.11/Server", rs);
            System.out.println("Server Ready");

            readFile();
        } catch (RemoteException re) {
            System.out.println("Exception in RMIServer.main" + re);
        }
    }
}
