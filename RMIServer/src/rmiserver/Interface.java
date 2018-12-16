package rmiserver;

import shared.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Interface extends Remote {
    String helloWorld() throws RemoteException;

    String register(String username, String pass, String email, String name, Boolean edit) throws RemoteException;

    User login(String username, String pass) throws RemoteException;
    User loginDrop(String clientId) throws RemoteException;

    void logout(String username) throws RemoteException;

    //Saves clientInterface/onlineStatus
    void subscribe(String username, clientInterface cInterface) throws RemoteException;

    //send/save notification
    void sendNotifcation(Notification note, String username) throws RemoteException;

    //Deletes notifications on user line
    void clearDatabaseNotifications(String username) throws RemoteException;

    //Get Notifications
    ArrayList<Notification> getNotifications(String username) throws RemoteException;

    String addDropbox(String username, String dropbox_id, String dropbox_token) throws RemoteException;
    String getDropboxId(String username) throws RemoteException;

    String playlistMethods(String method, String word, String music, String username) throws RemoteException;

    //AddMusic Method
    String addFile(String dropbox_id, String username, int music_id) throws RemoteException;
    String addAlbum(String title, String releaseDate, String description, String artist) throws RemoteException;
    String addArtist(String name, String details, int solo) throws RemoteException;
    String addMusic(String name, String genre, String length, String lyrics, String album) throws RemoteException;
    String addReview(Review review, Boolean isCreated) throws RemoteException;

    String addPlaylist(String name, String username) throws RemoteException;
    String addComposed(int music_id, int artist_id) throws RemoteException;
    String addFeatured(int music_id, int artist_id) throws RemoteException;
    String addWroteLyrics(int music_id, int artist_id) throws RemoteException;
    String addArtistToGroup(int artist_id, int group_id, String role) throws RemoteException;
    String addMusicToPlaylist(int music_id, int playlist_id) throws RemoteException;

    //Update Method
    String updateAlbum(Album album, String username) throws RemoteException;
    String updateArtist(Artist artist, String username) throws RemoteException;
    String updateMusic(Music music, String username) throws RemoteException;

    //Delete Method
    String deleteAlbum(int id) throws RemoteException;
    String deleteArtist(int id, ArrayList<Integer> arr) throws RemoteException;
    String deleteMusic(int id) throws RemoteException;

    String deletePlaylist(int id) throws RemoteException;
    String deleteComposed(int artist_id, int music_id) throws RemoteException;
    String deleteFeature(int artist_id, int music_id) throws RemoteException;
    String deleteWroteLyrics(int artist_id, int music_id) throws RemoteException;
    String deleteMusicPlaylist(int music_id, int playlist_id) throws RemoteException;
    String removeArtistFromGroup(int artist_id, int group_id) throws RemoteException;

    //Search Method
    ArrayList<Album> searchAlbum(String word)throws RemoteException;
    ArrayList<Album> searchAlbum(int id)throws RemoteException;
    ArrayList<Music> searchMusic(String word)throws RemoteException;
    ArrayList<Music> searchMusic(int album_id)throws RemoteException;
    ArrayList<Artist> searchArtist(String word)throws RemoteException;
    ArrayList<Artist> searchArtist(int id)throws RemoteException;
    Review searchReview(String username, int album_id)throws RemoteException;
    ArrayList<Review> searchReview(int album_id)throws RemoteException;
    ArrayList<Playlist> searchPlaylist(String name, String username) throws RemoteException;
    ArrayList<Music> searchMusicPlaylist(int playlist_id) throws RemoteException;

    ArrayList<Artist> searchSpecificArtist(String artist, boolean solo) throws RemoteException;
    ArrayList<Artist> searchArtistsFromGroup(int group_id) throws RemoteException;
    ArrayList<Artist> searchComposed(int music_id) throws RemoteException;
    ArrayList<Artist> searchFeatured(int music_id) throws RemoteException;
    ArrayList<Artist> searchWroteLyrics(int music_id) throws RemoteException;

    Boolean isAlive() throws RemoteException;

    String shareFile(String username, int music_id, String file_user_username) throws RemoteException;
    boolean searchFile(String username, int music_id) throws RemoteException;
    int searchUser(String username) throws RemoteException;
    ArrayList<User> searchUsers(String username) throws RemoteException;
    ArrayList<String> searchUserFile(String username, int music_id) throws RemoteException;
    String downloadFile(String username, int music_id, String ip, int port) throws RemoteException;
    String askIP() throws RemoteException;

}
