import java.io.*;
import java.lang.reflect.Array;
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

    public String sendMulticast(String message) {
        // Send it to multicast servers

        try {
            int serverIdIndex = ThreadLocalRandom.current().nextInt(0, idsMulticast.size());
            String serverID = idsMulticast.get(serverIdIndex);
            message += ";serverID|" + serverID;
            int id = messageID.getAndAdd(2);
            message += ";messageID|" + Integer.toString(id);
            byte[] sendBuffer = message.getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, group, MULTICAST_PORT);
            this.senderSocket.send(packet);
            // Waits for multicast servers to respond
            byte[] receiveBuffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            do {
                this.receiverSocket.receive(request);
            } while (!checkMessageID(id, request));
            String response = new String(request.getData(), 0, request.getLength());
            int i;
            for (i = response.length() - 1; i >= 0; i--) {
                if (response.charAt(i) == ';') {
                    break;
                }
            }
            response = response.substring(0, i);
            System.out.println(response);
            return response;
        } catch (UnknownHostException ue) {
            System.out.println(ue);
            return "error";
        } catch (IOException e) {
            System.out.println(e);
            return "error";
        }
    }

    public void sendBroadcast() {
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
            while (true) {
                try {
                    s.receive(request);
                    idsMulticast.add(new String(request.getData(), 0, request.getLength()));
                } catch (SocketTimeoutException e) {
                    // timeout exception.
                    s.close();
                    System.out.println(idsMulticast);
                    break;
                }
            }
        } catch (UnknownHostException ue) {
            System.out.println(ue);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String helloWorld() throws RemoteException {
        String t = "function|search;what|user;where|username='francisco' AND password|'test'";
        return sendMulticast(t);
    }

    public String register(String username, String pass, String email, String name, Boolean edit) {
        String t = "function|create;what|user;username|" + username + ";password|" + pass + ";email|" + email + ";name|" + name + ";personalinfo|empty;editor|0";
        String answer = sendMulticast(t);
        if (answer.equalsIgnoreCase("Error")) {
            return "Error";
        }
        System.out.println("New user created");
        return "Sucess";

    }

    public User login(String username, String pass) throws RemoteException {
        for (User temp : onlineUsers) {
            System.out.println(username + "  " + temp.username);
            if (temp.username.equalsIgnoreCase(username)) {
                System.out.println("The user " + temp.username + " is already Online");
                return null;
            }
        }

        String t = "function|search;what|user;where|username='" + username + "' AND password='" + pass + "'";
        User tempUser;
        //Send through Multicast
        String user = sendMulticast(t);
        if (user.equalsIgnoreCase("Error") || user.equals("nothing")) {
            tempUser = null;
        } else {
            Map<String, String> answer = UDP.protocolToHash(user);
            Boolean editor = true;
            System.out.println(answer.get("editor"));
            if (answer.get("editor").equals("0")) editor = false;
            tempUser = new User(answer.get("username"), answer.get("password"), answer.get("email"), answer.get("name"), editor);
            onlineUsers.add(tempUser);
        }
        writeFile();
        return tempUser;
    }

    public void logout(String username) throws RemoteException {
        for (User temp : onlineUsers) {
            if (temp.username.equalsIgnoreCase(username)) {
                onlineUsers.remove(temp);
            }
        }
        writeFile();
    }

    public void subscribe(String username, clientInterface cInterface) throws RemoteException {
        //Function that connects user with its interface
        for (User temp : onlineUsers) {
            if (temp.username.equalsIgnoreCase(username)) {
                temp.cInterface = cInterface;
            }
        }
    }

    public void sendNotifcation(Notification note, String username) throws RemoteException {
        for (User temp : onlineUsers) {
            if (temp.username.equalsIgnoreCase(username)) {
                if (temp.isEditor()) {
                    System.out.println("This user is already an editor");
                    return;
                }
                temp.editor = true;
                String t = "function|update;what|user;set|editor=1;where|username='" + username + "'";
                String answer = sendMulticast(t);
                System.out.println(answer);
                try {
                    temp.cInterface.liveNotification(note.text);
                } catch (RemoteException e) {
                    t = "function|create;what|notification;id|null;text|" + note.text + ";user_username|" + username;
                    answer = sendMulticast(t);
                    System.out.println(answer);
                }
                return;
            }
        }

        String t = "function|update;what|user;set|editor=1;where|username='" + username + "'";
        String answer = sendMulticast(t);
        System.out.println(answer);

        t = "function|create;what|notification;id|null;text|" + note.text + ";user_username|" + username;
        answer = sendMulticast(t);
        System.out.println(answer);

    }

    public void sendNotifcationEdits(String type, int id, String username) {
        switch (type) {
            case "album": {
                String t = "function|search;what|user_album;where|album_id=" + Integer.toString(id) + " AND user_username='" + username + "'";
                String check = sendMulticast(t);
                if (check.equals("") || check.equals("nothing")) {
                    t = "function|create;what|user_album;user_username|" + username + ";album_id|" + id;
                    sendMulticast(t);
                }
                t = "function|search;what|user_album;where|album_id=" + Integer.toString(id) + " AND user_username!='" + username + "'";
                check = sendMulticast(t);
                String note = "The album with album id (" + Integer.toString(id) + ") has beed edited";
                checkNotificationEdits(check, note, username);
                break;
            }
            case "music": {
                String t = "function|search;what|user_music;where|music_id=" + Integer.toString(id) + " AND user_username='" + username + "'";
                String check = sendMulticast(t);
                if (check.equals("") || check.equals("nothing")) {
                    t = "function|create;what|user_music;user_username|" + username + ";music_id|" + id;
                    sendMulticast(t);
                }
                t = "function|search;what|user_music;where|music_id=" + Integer.toString(id) + " AND user_username!='" + username + "'";
                check = sendMulticast(t);
                String note = "The music with music id (" + Integer.toString(id) + ") has beed edited";
                checkNotificationEdits(check, note, username);
                break;
            }
            case "artist": {
                String t = "function|search;what|user_artist;where|artist_id=" + Integer.toString(id) + " AND user_username='" + username + "'";
                String check = sendMulticast(t);
                if (check.equals("") || check.equals("nothing")) {
                    t = "function|create;what|user_artist;user_username|" + username + ";artist_id|" + id;
                    sendMulticast(t);
                }
                t = "function|search;what|user_artist;where|artist_id=" + Integer.toString(id) + " AND user_username!='" + username + "'";
                check = sendMulticast(t);
                String note = "The artist with artist id (" + Integer.toString(id) + ") has beed edited";
                checkNotificationEdits(check, note, username);
                break;
            }
        }
    }

    public void checkNotificationEdits(String info, String note, String username) {
        boolean checkSend;
        if (!info.equals("") && !info.equalsIgnoreCase("nothing")) {
            String objects[] = info.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                checkSend = false;
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                for (User user : onlineUsers) {
                    if (user.username.equals(arr.get("user_username"))) {
                        checkSend = true;
                        try {
                            user.cInterface.liveNotification(note);
                        } catch (RemoteException e) {
                            String t = "function|create;what|notification;id|null;text|" + note + ";user_username|" + username;
                            sendMulticast(t);
                        }
                        break;
                    }
                }
                if (!checkSend) {
                    String t = "function|create;what|notification;id|null;text|" + note + ";user_username|" + arr.get("user_username");
                    sendMulticast(t);
                }
            }
        }
    }

    public void clearDatabaseNotifications(String username) throws RemoteException {
        String t = "function|delete;what|notification;where|user_username='" + username + "'";
        String answer = sendMulticast(t);
        if (answer.equalsIgnoreCase("Error")) {
            System.out.println("Error");
        } else {
            System.out.println("Success");
        }
    }

    public ArrayList<Notification> getNotifications(String username) throws RemoteException {
        String t = "function|search;what|notification;where|user_username='" + username + "'";
        String notifications = sendMulticast(t);
        ArrayList<Notification> notes = new ArrayList<>();
        if (notifications != "") {
            String objects[] = notifications.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Notification temp = new Notification(arr.get("username"), arr.get("text"));
                notes.add(temp);
            }
        }
        return notes;
    }

    public String playlistMethods(String method, String title, String music, String username) throws RemoteException {
        String t;
        switch (method) {
            case "create":
                /*
                 * title = playlist user = user Go to Database and create Line
                 */
                t = "function|create;what|playlist;title|" + title + ";username|" + username;
                return "Playlist Created!";
            case "delete":
                /*
                 * title = playlist user = user Go to database and delete Line What happens if
                 * it doesnt exist?
                 */

                t = "function|delete;what|playlist;where|title='" + title + "'";

                return "Playlist deleted";
            case "add":

                t = "function|search;what|playlist;where|title='" + title + "'";
                //playlistID = playlist.ID
                //if null return "error"

                t = "function|search;what|music;where|name='" + music + "'";
                //musicID = music.ID
                //if null return "error"

                //t = "create | playlist_music ; title_id | "+playlistID+" ; music_id | "+musicID+" ;";

                /*
                 * Go to database and add
                 * */
                return "Added " + music + " to " + title;
            case "remove":

                t = "function|search;what|playlist;where|title='" + title + "'";
                //playlistID = playlist.ID
                //if null return "error"

                t = "function|search;what|music;where|name='" + music + "'";
                //musicID = music.ID
                //if null return "error"

                //t = "function|delete;what|playlist_music;where|title="+playlistID+" AND music="+musicID;

                /*
                 * Go to database and SWITCH title and music with respective ID and delete that song
                 * */
                return "Deleted " + music + " from " + title;
            default:
                return "Method Does Not exist!";
        }
    }

    // Add functions

    public String addAlbum(String title, String releaseDate, String description, String artist) throws RemoteException {
        int artistid;
        String t = "function|search;what|artist;where|name='" + artist + "'";
        String answer = sendMulticast(t);
        if (!answer.equals("nothing")) {
            Map<String, String> arr = UDP.protocolToHash(answer);
            artistid = Integer.parseInt(arr.get("id"));
        } else {
            System.out.println("Error");
            return "Error - Artist doesn't exist";
        }

        t = "function|create;what|album;id|null;title|" + title + ";releasedate|" + releaseDate + ";description|" + description + ";artist_id|" + artistid;
        answer = sendMulticast(t);
        return answer;
    }

    public String addArtist(String name, String details) throws RemoteException {
        String t = "function|create;what|artist;id|null;name|" + name + ";details|" + details;
        String answer = sendMulticast(t);
        return answer;
    }

    public String addMusic(String name, String genre, String length, String lyrics, String album) throws RemoteException {
        int albumid;
        String t = "function|search;what|album;where|title='" + album + "'";
        String answer = sendMulticast(t);
        if (!answer.equals("nothing")) {
            Map<String, String> arr = UDP.protocolToHash(answer);
            albumid = Integer.parseInt(arr.get("id"));
        } else {
            System.out.println("Error");
            return "Error - Album doesn't exist";
        }

        t = "function|create;what|music;id|null;name|" + name + ";genre|" + genre + ";length|" + length + ";lyrics|" + lyrics + ";album_id| " + albumid;
        answer = sendMulticast(t);
        return answer;
    }

    public String addReview(Review review, Boolean isCreate) {
        String t, answer;
        if (isCreate) {
            t = "function|create;what|review;id|null;text|" + review.getText() + ";rating|" + review.getRating() + ";datee|CURRENT_TIME();album_id|" + review.getAlbum_id() + ";user_username|" + review.getUsername();
        } else {
            t = "function|update;what|review;set|text='" + review.getText() + "'" + ",rating=" + review.getRating() + ",datee=CURRENT_TIME();where|album_id=" + review.getAlbum_id() + " AND user_username='" + review.getUsername() + "'";
        }
        answer = sendMulticast(t);
        return answer;
    }

    public String addPlaylist(String name, String username){
        String t = "function|create;what|playlist;id|null;name|"+name+";user_username|"+username;
        String answer = sendMulticast(t);
        return answer;
    }

    public String addMusicToPlaylist(int music_id, int playlist_id){
        String t = "function|create;what|playlist;music_id|"+music_id+";playlist_id|"+playlist_id;
        String answer = sendMulticast(t);
        return answer;
    }

    public String addComposed(int music_id, int artist_id){
        String t = "function|create;what|composed;artist_id|"+artist_id+";music_id|"+music_id;
        String answer = sendMulticast(t);
        return answer;
    }

    public String addFeatured(int music_id, int artist_id){
        String t = "function|create;what|featured;artist_id|"+artist_id+";music_id|"+music_id;
        String answer = sendMulticast(t);
        return answer;
    }

    public String addWroteLyrics(int music_id, int artist_id){
        String t = "function|create;what|wrote_lyrics;artist_id|"+artist_id+";music_id|"+music_id;
        String answer = sendMulticast(t);
        return answer;
    }

    public String addArtistToGroup(int artist_id, int group_id, String role){
        String t = "function|create;what|role;role|"+role+";artist_id|"+artist_id+";group_id|"+group_id;
        String answer = sendMulticast(t);
        return answer;
    }

    // Update functions

    public String updateAlbum(Album album, String username) {
        String t = "function|update;what|album;set|title='" + album.title + "',releasedate='" + album.releaseDate + "',description='" + album.description + "';where|id=" + album.id;
        String answer = sendMulticast(t);
        sendNotifcationEdits("album", album.id, username);
        return answer;
    }

    public String updateArtist(Artist artist, String username) {
        String t = "function|update;what|artist;set|name='" + artist.name + "',details='" + artist.details + "';where|id=" + artist.id;
        String answer = sendMulticast(t);
        sendNotifcationEdits("artist", artist.id, username);
        return answer;
    }

    public String updateMusic(Music music, String username) {
        String t = "function|update;what|music;set|name='" + music.name + "',genre='" + music.type + "',length='" + music.length + "';where|id=" + music.id;
        String answer = sendMulticast(t);
        sendNotifcationEdits("music", music.id, username);
        return answer;
    }

    // Delete functions

    public String deleteMusic(int id) {
        String t = "function|delete;what|music_playlist;where|music_id="+id;
        sendMulticast(t);
        t = "function|delete;what|featured;where|music_id="+id;
        sendMulticast(t);
        t = "function|delete;what|composed;where|music_id="+id;
        sendMulticast(t);
        t = "function|delete;what|wrote_lyrics;where|music_id="+id;
        sendMulticast(t);
        t = "function|delete;what|user_editor_music;music_id="+id;
        sendMulticast(t);
        t = "function|delete;what|file;music_id="+id;
        sendMulticast(t);
        t = "function|delete;what|file_user;file_music_id="+id;
        sendMulticast(t);
        t = "function|delete;what|music;where|id=" + id;
        String answer = sendMulticast(t);
        return answer;
    }

    public String deleteAlbum(int id) {
        String t = "function|delete;what|music;where|album_id=" + id;
        sendMulticast(t);
        t = "function|delete;what|user_editor_album;album_id="+id;
        sendMulticast(t);
        t = "function|delete;what|album;where|id=" + id;
        String answer = sendMulticast(t);

        return answer;
    }

    public String deleteArtist(int id, ArrayList<Integer> arr) {
        String t;
        for (int i : arr) {
            t = "function|delete;what|music;where|album_id=" + i;
            sendMulticast(t);
        }
        t = "function|delete;what|album;where|artist_id=" + id;
        sendMulticast(t);
        t = "function|delete;what|role;where|artist_id=" + id + " OR group_id="+ id;
        sendMulticast(t);
        t = "function|delete;what|user_editor_artist;artist_id="+id;
        sendMulticast(t);
        t = "function|delete;what|artist;where|id=" + id;
        String answer = sendMulticast(t);


        return answer;
    }

    public String deletePlaylist(int id){
        String t = "function|delete;what|music_playlist;where|playlist_id=" + id;
        String answer = sendMulticast(t);
        t = "function|delete;what|playlist;where|id="+id;
        answer = sendMulticast(t);
        return answer;
    }

    public String deleteComposed(int artist_id, int music_id){
        String t = "function|delete;what|composed;where|artist_id="+artist_id+" AND music_id=" + music_id;
        String answer = sendMulticast(t);
        return answer;
    }

    public String deleteFeature(int artist_id, int music_id){
        String t = "function|delete;what|feature;where|artist_id="+artist_id+" AND music_id=" + music_id;
        String answer = sendMulticast(t);
        return answer;
    }

    public String deleteWroteLyrics(int artist_id, int music_id){
        String t = "function|delete;what|wrote_lyrics;where|artist_id="+artist_id+" AND music_id=" + music_id;
        String answer = sendMulticast(t);
        return answer;
    }

    // Search functions

    public ArrayList<Album> searchAlbum(String word) {
        String t = "function|search;what|album;where|title='" + word + "'";
        String answer = sendMulticast(t);
        ArrayList<Album> albuns = new ArrayList<>();
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Album temp = new Album(arr.get("title"), arr.get("releasedate"), arr.get("description"), arr.get("artist_id"), Integer.parseInt(arr.get("id")));
                albuns.add(temp);
            }
        }
        t = "function|search;what|artist;where|name='" + word + "'";
        answer = sendMulticast(t);
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            Map<String, String> artist = UDP.protocolToHash(answer);
            t = "function|search;what|album;where|artist_id=" + artist.get("id");
            answer = sendMulticast(t);
            if (answer != "" && !answer.equalsIgnoreCase("nothing")) {
                String objects[] = answer.split("\\*\\*");
                for (int i = 0; i < objects.length; i++) {
                    Map<String, String> arr = UDP.protocolToHash(objects[i]);
                    Album temp = new Album(arr.get("title"), arr.get("releasedate"), arr.get("description"), arr.get("artist_id"), Integer.parseInt(arr.get("id")));
                    albuns.add(temp);
                }
            }
        }
        return albuns;
    }

    public ArrayList<Album> searchAlbum(int id) {
        String t = "function|search;what|album;where|artist_id=" + id + "";
        String answer = sendMulticast(t);
        ArrayList<Album> albuns = new ArrayList<>();
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Album temp = new Album(arr.get("title"), arr.get("releasedate"), arr.get("description"), arr.get("artist_id"), Integer.parseInt(arr.get("id")));
                albuns.add(temp);
            }
        }
        return albuns;
    }

    public ArrayList<Music> searchMusic(String word) {
        String t = "function|search;what|music;where|name='" + word + "' OR genre='" + word + "'";
        String answer = sendMulticast(t);
        ArrayList<Music> musics = new ArrayList<>();
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Music temp = new Music(arr.get("name"), arr.get("genre"), Integer.parseInt(arr.get("length")), arr.get("lyrics"), Integer.parseInt(arr.get("album_id")), Integer.parseInt(arr.get("id")));
                musics.add(temp);
            }
        }
        return musics;
    }

    public ArrayList<Music> searchMusic(int album_id) {
        String t = "function|search;what|music;where|album_id=" + album_id;
        String answer = sendMulticast(t);
        ArrayList<Music> musics = new ArrayList<>();
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Music temp = new Music(arr.get("name"), arr.get("genre"), Integer.parseInt(arr.get("length")), arr.get("lyrics"), Integer.parseInt(arr.get("album_id")), Integer.parseInt(arr.get("id")));
                musics.add(temp);
            }
        }
        return musics;
    }

    public ArrayList<Review> searchReview(int album_id) {
        String t = "function|search;what|review;where|album_id=" + album_id;
        String answer = sendMulticast(t);
        ArrayList<Review> reviews = new ArrayList<>();
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Review temp = new Review(arr.get("user_username"), Integer.parseInt(arr.get("album_id")), arr.get("text"), Integer.parseInt(arr.get("rating")), arr.get("datee"));
                reviews.add(temp);
            }
        }
        return reviews;
    }

    public Review searchReview(String username, int album_id) {
        String t = "function|search;what|review;where|album_id=" + album_id + " AND user_username='" + username + "'";
        String answer = sendMulticast(t);
        Review review = null;
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                review = new Review(arr.get("user_username"), Integer.parseInt(arr.get("album_id")), arr.get("text"), Integer.parseInt(arr.get("rating")), arr.get("datee"));
            }
        }
        return review;
    }

    public ArrayList<Artist> searchArtist(String word) {
        String t = "function|search;what|artist;where|name='" + word + "'";
        String answer = sendMulticast(t);
        ArrayList<Artist> artists = new ArrayList<>();
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Artist temp = new Artist(arr.get("name"), arr.get("details"), Integer.parseInt(arr.get("id")));
                artists.add(temp);
            }
        }
        return artists;
    }

    public boolean searchFile(String username, int music_id) {
        String t = "function|search;what|file;where|user_username='" + username + "' AND music_id=" + music_id;
        String answer = sendMulticast(t);
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            return true;
        }
        return false;
    }

    public ArrayList<String> searchUserFile(String username, int music_id) {
        String t = "function|search;what|file_user;where|user_username='" + username + "' AND file_music_id=" + music_id;
        String answer = sendMulticast(t);
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            ArrayList<String> usernames = new ArrayList<>();
            String objects[] = answer.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                String temp = new String(arr.get("file_user_username"));
                usernames.add(temp);
            }
        }
        return null;
    }

    public int searchUser(String username) {
        String t = "function|search;what|user;where|username='" + username + "'";
        String answer = sendMulticast(t);
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            return 1;
        }
        return -1;
    }

    public ArrayList<Playlist> searchPlaylist(String name){
        String t = "function|search;what|playlist;where|name='" + name + "'";
        String answer = sendMulticast(t);
        ArrayList<Playlist> playlists = new ArrayList<>();
        if (!answer.equals("") && !answer.equalsIgnoreCase("nothing")) {
            String objects[] = answer.split("\\*\\*");
            for (int i = 0; i < objects.length; i++) {
                Map<String, String> arr = UDP.protocolToHash(objects[i]);
                Playlist temp = new Playlist(arr.get("user_username"), arr.get("name"));
                playlists.add(temp);
            }
        }
        return playlists;
    }

    public String shareFile(String username, int music_id, String file_user_username) {
        return sendMulticast("function|create;what|file_user;user_username|" + username + ";music_id|" + music_id + ";file_user_username|" + file_user_username);
    }

    public String downloadFile(String username, int music_id, String ip, int port) {
        boolean file = searchFile(username, music_id);
        if (file) {
            sendMulticast("function|download;ip|" + ip + ";port|" + Integer.toString(port) + ";username|" + username + ";music_id|"+music_id);
            return "Success";
        } else {
            ArrayList<String> usernames = searchUserFile(username, music_id);
            if (usernames == null) {
                return "You can't download that music";
            } else {
                sendMulticast("function|download;ip|" + ip + ";port|" + Integer.toString(port) + ";username|" + usernames.get(0) + ";music_id|"+music_id);
                return "Success";
            }
        }
    }

    public String askIP() {
        return sendMulticast("function|askIP");
    }

    public boolean checkMessageID(int id, DatagramPacket request) {
        String receive = new String(request.getData(), 0, request.getLength());
        if (receive.equals("")) {
            return false;
        }
        int i;
        for (i = receive.length() - 1; i >= 0; i--) {
            if (receive.charAt(i) == ';') {
                break;
            }
        }
        receive = receive.substring(i);
        String parts[] = receive.split("\\|");
        return Integer.toString(id + 1).equals(parts[1].trim());
    }

    public Boolean isAlive() throws RemoteException {
        return true;
    }

    public void writeFile() {
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

    public static void readFile() {
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
            Network newNet = new Network();
            System.setProperty("java.rmi.server.hostname", newNet.getIP());
            System.out.println(newNet.getIP());
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
            if (i.isAlive()) {
            } // If its still not existing, keep increasing counts

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

