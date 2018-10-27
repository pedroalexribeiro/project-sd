import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Interface extends Remote {
    String helloWorld() throws RemoteException;

    String register(String username,String pass,String email,String name,Boolean edit) throws RemoteException;

    User login(String username,String pass) throws RemoteException;

    void logout(String username) throws RemoteException;

    //Saves clientInterface/onlineStatus
    void subscribe(String username,clientInterface cInterface) throws RemoteException;

    //send/save notification
    void sendNotifcation(Notification note,String username) throws RemoteException;

    //Deletes notifications on user line
    void clearDatabaseNotifications(String username) throws RemoteException;

    //Get Notifications
    ArrayList<Notification> getNotifications(String username) throws RemoteException;


    String playlistMethods(String method,String word,String music,String username) throws RemoteException;

    //Add Method
    String addAlbum(String title,String releaseDate,String description,String artist) throws RemoteException;
    String addArtist(String name,String details) throws RemoteException;
    String addMusic(String name,String genre,String length,String album) throws RemoteException;
    String addReview(Review review,Boolean isCreated) throws RemoteException;

    //Update Method
    String updateAlbum(Album album) throws RemoteException;
    String updateArtist(Artist artist) throws RemoteException;
    String updateMusic(Music music) throws RemoteException;

    //Search Method
    ArrayList<Album> searchAlbum(String word)throws RemoteException;
    ArrayList<Music> searchMusic(String word)throws RemoteException;
    ArrayList<Music> searchMusic(int album_id)throws RemoteException;
    ArrayList<Artist> searchArtist(String word)throws RemoteException;
    Review searchReview(String username, int album_id)throws RemoteException;
    ArrayList<Review> searchReview(int album_id)throws RemoteException;

    Boolean isAlive() throws RemoteException;

    String shareFile(String username, int music_id, int file_id) throws RemoteException;
    int searchFile(String username, int music_id) throws RemoteException;
    int searchUser(String username) throws RemoteException;
    int searchUserFile(String username, int music_id) throws RemoteException;
    String downloadFile(String username, int music_id, String ip, int port) throws RemoteException;
    String askIP() throws RemoteException;

}
