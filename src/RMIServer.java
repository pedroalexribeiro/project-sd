import java.io.*;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RMIServer extends UnicastRemoteObject implements Interface {

    private static final long serialVersionUID = 1L;

    private static CopyOnWriteArrayList<User> onlineUsers = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<String> idsMulticast = new CopyOnWriteArrayList<>();

    private AtomicInteger messageID = new AtomicInteger(1000);

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
            sendBroadcast();
        } catch (IOException e) {
            e.printStackTrace();
            this.receiverSocket.close();
            this.senderSocket.close();
        }
    }

    public String sendMulticast(String message){
        // Send it to multicast servers

        try {
            int serverIdIndex = ThreadLocalRandom.current().nextInt(0, idsMulticast.size());
            String serverID = idsMulticast.get(serverIdIndex);
            message += ";serverID|"+serverID;
            int id = messageID.getAndAdd(2);
            message += ";messageID|" + Integer.toString(id);
            byte[] sendBuffer = message.getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, group, MULTICAST_PORT);
            this.senderSocket.send(packet);




            // Waits for multicast servers to respond
            byte[] receiveBuffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            do{
                this.receiverSocket.receive(request);
            }while (!checkMessageID(id, request));
            String response = new String(request.getData(), 0, request.getLength());
            int i;
            for(i = response.length()-1; i >= 0; i-- ){
                if(response.charAt(i) == ';'){
                    break;
                }
            }
            response = response.substring(0, i);
            System.out.println(response);
            return response;
        }catch(UnknownHostException ue){
            System.out.println(ue);
            return "error";
        }catch(IOException e){
            System.out.println(e);
            return "error";
        }
    }

    public void sendBroadcast(){
        DatagramSocket s;
        String message = "function|broadcast";
        try {
            s = new DatagramSocket(20000);
            byte[] sendBuffer = message.getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, group, MULTICAST_PORT);
            s.send(packet);

            // Waits for multicast servers to respond
            byte[] receiveBuffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            s.setSoTimeout(5000);
            while(true){
                try {
                    s.receive(request);
                    idsMulticast.add(new String(request.getData(), 0, request.getLength()));
                }catch (SocketTimeoutException e) {
                    // timeout exception.
                    s.close();
                    System.out.println(idsMulticast);
                    break;
                }
            }
        }catch(UnknownHostException ue){
            System.out.println(ue);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public String helloWorld() throws RemoteException {
        String t = "function|search;what|user;where|username='francisco' AND password|'test'";
        return sendMulticast(t);
    }

    public String register(String username, String pass, String email, String name, Boolean edit) {
        String t = "function|create;what|user;username|"+username+";password|"+pass+";email|"+email+";name|"+name+";personalinfo|empty;editor|0";
        String answer = sendMulticast(t);
        if(answer.equalsIgnoreCase("Error")){
            return "Error";
        }
        System.out.println("New user created");
        return "Sucess";

    }

    public User login(String username, String pass) throws RemoteException {
        for(User temp : onlineUsers){
            System.out.println(username +"  "+temp.username);
            if (temp.username.equalsIgnoreCase(username)){
                System.out.println("The user "+ temp.username +" is already Online");
                return null;
            }
        }

        String t = "function|search;what|user;where|username='"+username+"' AND password='"+pass+"'";
        User tempUser;
        //Send through Multicast
        String user = sendMulticast(t);
        if(user.equalsIgnoreCase("Error") || user.equals("nothing")){
            tempUser =  null;
        }
        else{
            Map<String, String> answer = UDP.protocolToHash(user);
            Boolean editor = true;
            System.out.println(answer.get("editor"));
            if(answer.get("editor").equals("0")) editor = false;
            tempUser =  new User(answer.get("username"),answer.get("password"),answer.get("email"),answer.get("name"),editor);
            onlineUsers.add(tempUser);
        }
        writeFile();
        return tempUser;
    }

    public void logout(String username) throws RemoteException{
        for(User temp : onlineUsers){
            if (temp.username.equalsIgnoreCase(username)){
                onlineUsers.remove(temp);
            }
        }
        writeFile();
    }

    public void subscribe(String username, clientInterface cInterface) throws RemoteException {
        //Function that connects user with its interface
        for(User temp : onlineUsers){
            if (temp.username.equalsIgnoreCase(username)){
                temp.cInterface = cInterface;
            }
        }
    }

    public void sendNotifcation(Notification note, String username) throws RemoteException {
        for(User temp : onlineUsers){
            if (temp.username.equalsIgnoreCase(username)){
                if(temp.isEditor()) {
                    System.out.println("This user is already an editor");
                    return;
                }
                temp.editor=true;
                String t = "function|update;what|user;set|editor=1;where|username='"+username+"'";
                String answer = sendMulticast(t);
                System.out.println(answer);
                temp.cInterface.liveNotification(note);
                return;
            }
        }

        String t = "function|update;what|user;set|editor=1;where|username='"+username+"'";
        String answer = sendMulticast(t);
        System.out.println(answer);

         t = "function|create;what|notification;id|null;text|"+note.text+";user_username|"+username;
         answer = sendMulticast(t);
         System.out.println(answer);

    }

    /*public void sendNotifcation(Notification note, ArrayList<String> usernames) throws RemoteException {

        for(String user : usernames) {
            for (User temp : onlineUsers) {
                if (temp.username.equalsIgnoreCase(username) && !temp.isEditor()) {
                    //String t = "function|update;what|notification;text | "+note.text+" ; user_username | "+username+" ;";
                    //String answer = sendMulticast(t);
                    temp.cInterface.liveNotification(note);
                    return;
                }
            }
        }
        String t = "create | notification ; text | "+note.text+" ; user_username | "+username+" ;";
        String answer = sendMulticast(t);
        System.out.println(answer);

    }*/


    public void clearDatabaseNotifications(String username) throws RemoteException {
        String t = "function|delete;what|notification;where|user_username='" + username + "'";
        String answer = sendMulticast(t);
        if(answer.equalsIgnoreCase("Error")){
            System.out.println("Error");
        }
        else{
            System.out.println("Success");
        }
    }

    public ArrayList<Notification> getNotifications(String username)throws RemoteException{
        String t = "function|search;what|notification;where|user_username='"+username+"'";
        String notifications = sendMulticast(t);
        ArrayList<Notification> notes = new ArrayList<>();
        if(notifications != "") {
            String objects[] = notifications.split("\\*\\*");
            for(int i=0; i < objects.length; i++){
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Notification temp = new Notification(arr.get("username"), arr.get("text"));
                notes.add(temp);
            }
        }
        return notes;
    }

    public String playlistMethods(String method, String title, String music, String username)  throws RemoteException{
        String t;
        switch (method) {
        case "create":
            /*
             * title = playlist user = user Go to Database and create Line
             */
             t = "function|create;what|playlist;title|"+title+";username|"+username;
            return "Playlist Created!";
        case "delete":
            /*
             * title = playlist user = user Go to database and delete Line What happens if
             * it doesnt exist?
             */

             t = "function|delete;what|playlist;where|title='"+title + "'";

            return "Playlist deleted";
        case "add":

            t = "function|search;what|playlist;where|title='"+title + "'";
            //playlistID = playlist.ID
            //if null return "error"

            t = "function|search;what|music;where|name='"+music+"'";
            //musicID = music.ID
            //if null return "error"

            //t = "create | playlist_music ; title_id | "+playlistID+" ; music_id | "+musicID+" ;";

            /*
            * Go to database and add
            * */
            return "Added " + music + " to " + title;
        case "remove":

            t = "function|search;what|playlist;where|title='"+title+"'";
            //playlistID = playlist.ID
            //if null return "error"

            t = "function|search;what|music;where|name='"+music+"'";
            //musicID = music.ID
            //if null return "error"

            //t = "function|delete;what|playlist_music;where|title="+playlistID+" AND music="+musicID;

            /*
             * Go to database and SWITCH title and music with respective ID and delete that song
             * */
            return "Deleted " + music + " from "+title;
        default:
            return "Method Does Not exist!";
        }
    }

    public String addAlbum(String title,String releaseDate,String description,String artist) throws RemoteException{
        int artistid;
        String t = "function|search;what|artist;where|name='"+artist+"'";
        String answer = sendMulticast(t);
        if(!answer.equals("nothing")){
            Map<String, String> arr = UDP.protocolToHash(answer);
            artistid = Integer.parseInt(arr.get("id"));
        } else{
            System.out.println("Error");
            return "Error - Artist doesn't exist";
        }

        t = "function|create;what|album;id|null;title|"+title+";releasedate|"+releaseDate+";description|"+description + ";artist_id|"+artistid;
        answer = sendMulticast(t);
        return answer;
    }

    public String addArtist(String name,String details) throws RemoteException{
        String t = "function|create;what|artist;id|null;name|"+name+";details|"+details;
        String answer = sendMulticast(t);
        return answer;
    }

    public String addMusic(String name,String genre,String length,String album) throws RemoteException{
        int albumid;
        String t = "function|search;what|album;where|title='"+album+"'";
        String answer = sendMulticast(t);
        if(!answer.equals("")){
            Map<String, String> arr = UDP.protocolToHash(answer);
            albumid = Integer.parseInt(arr.get("id"));
        } else{
            System.out.println("Error");
            return "Error - Album doesn't exist";
        }

        t = "function|create;what|music;id|null;name|"+name+";genre|"+genre+";length|"+length + ";album_id| "+albumid;
        answer = sendMulticast(t);
        return answer;
    }

    public String addReview(Review review,Boolean isCreate){
        String t,answer;
        if(isCreate){
            t = "function|create;what|review;id|null;text|"+review.getText()+";rating|"+review.getRating()+";datee|CURRENT_TIME();album_id|"+review.getAlbum_id()+";user_username|"+review.getUsername();
        }else{
            t = "function|update;what|review;set|text='"+review.getText()+"'"+",rating="+review.getRating()+",datee=CURRENT_TIME();where|album_id="+review.getAlbum_id()+" AND user_username='"+review.getUsername()+"'";
        }
        answer = sendMulticast(t);
        return answer;
    }

    public String updateAlbum(Album album){
        String t = "function|update;what|album;set|title='"+ album.title+"',releasedate='"+album.releaseDate+"',description='"+album.description+"';where|id="+album.id;
        String answer = sendMulticast(t);
        return answer;
    }

    public ArrayList<Album> searchAlbum(String word) {
        String t = "function|search;what|album;where|title='" + word + "'";
        String answer = sendMulticast(t);
        ArrayList<Album> albuns = new ArrayList<>();
        if(!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for(int i=0; i < objects.length; i++){
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Album temp = new Album(arr.get("title"), arr.get("releasedate"), arr.get("description"), arr.get("artist_id"),Integer.parseInt(arr.get("id")));
                albuns.add(temp);
            }
        }
        t = "function|search;what|artist;where|name='" + word + "'";
        answer = sendMulticast(t);
        if(!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            Map<String, String> artist = UDP.protocolToHash(answer);
            t = "function|search;what|album;where|artist_id=" + artist.get("id");
            answer = sendMulticast(t);
            if (answer != "" && !answer.equalsIgnoreCase("nothing")) {
                String objects[] = answer.split("\\*\\*");
                for (int i = 0; i < objects.length; i++) {
                    Map<String, String> arr = UDP.protocolToHash(objects[i]);
                    Album temp = new Album(arr.get("title"), arr.get("releasedate"), arr.get("description"), arr.get("artist_id"),Integer.parseInt(arr.get("id")));
                    albuns.add(temp);
                }
            }
        }
        return albuns;
    }

    public ArrayList<Music> searchMusic(String word) {
        String t = "function|search;what|music;where|name='" + word + "' OR genre='" + word + "'";
        String answer = sendMulticast(t);
        ArrayList<Music> musics = new ArrayList<>();
        if(!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for(int i=0; i < objects.length; i++){
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Music temp = new Music(arr.get("name"),arr.get("genre"),Integer.parseInt(arr.get("length")),Integer.parseInt(arr.get("album_id")));
                musics.add(temp);
            }
        }
        return musics;
    }

    public ArrayList<Music> searchMusic(int album_id) {
        String t = "function|search;what|music;where|album_id=" + album_id;
        String answer = sendMulticast(t);
        ArrayList<Music> musics = new ArrayList<>();
        if(!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for(int i=0; i < objects.length; i++){
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Music temp = new Music(arr.get("name"),arr.get("genre"),Integer.parseInt(arr.get("length")),Integer.parseInt(arr.get("album_id")));
                musics.add(temp);
            }
        }
        return musics;
    }

    public ArrayList<Review> searchReview(int album_id) {
        String t = "function|search;what|review;where|album_id=" + album_id;
        String answer = sendMulticast(t);
        ArrayList<Review> reviews = new ArrayList<>();
        if(!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for(int i=0; i < objects.length; i++){
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Review temp = new Review(arr.get("user_username"), Integer.parseInt(arr.get("album_id")),arr.get("text"), Integer.parseInt(arr.get("rating")), arr.get("datee"));
                reviews.add(temp);
            }
        }
        return reviews;
    }

    public Review searchReview(String username, int album_id) {
        String t = "function|search;what|review;where|album_id=" + album_id + " AND user_username='" + username +"'";
        String answer = sendMulticast(t);
        Review review=null;
        if(!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for(int i=0; i < objects.length; i++){
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                review = new Review(arr.get("user_username"), Integer.parseInt(arr.get("album_id")),arr.get("text"), Integer.parseInt(arr.get("rating")), arr.get("datee"));
            }
        }
        return review;
    }

    public ArrayList<Artist> searchArtist(String word) {
        String t = "function|search;what|artist;where|name='" + word + "'";
        String answer = sendMulticast(t);
        ArrayList<Artist> artists = new ArrayList<>();
        if(!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for(int i=0; i < objects.length; i++){
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Artist temp = new Artist(arr.get("name"), arr.get("details"));
                artists.add(temp);
            }
        }
        return artists;
    }

    public String askIP(){
        return sendMulticast("function|getIP");
    }

    public boolean checkMessageID(int id, DatagramPacket request){
        String receive = new String(request.getData(), 0, request.getLength());
        if(receive.equals("")){
            return false;
        }
        int i;
        for(i = receive.length()-1; i >= 0; i-- ){
            if(receive.charAt(i) == ';'){
                break;
            }
        }
        receive = receive.substring(i);
        String parts[] = receive.split("\\|");
        return Integer.toString(id+1).equals(parts[1].trim());
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
            }
        catch (Exception e) {
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
            //Network net = new Network();
            System.getProperties().put("java.security.policy", "java.policy.applet");
            System.setSecurityManager(new RMISecurityManager());
            System.setProperty("java.rmi.server.hostname","localhost");
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

            readFile();
        } catch (RemoteException re) {
            System.out.println("Exception in RMIServer.main" + re);
        }
    }
}

