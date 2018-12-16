package web.model;

import rmiserver.Interface;
import shared.Album;
import shared.Artist;
import shared.Music;
import shared.Review;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class MusicBean {
    private Interface server;

    public MusicBean() {
        try {
            server = (Interface) LocateRegistry.getRegistry("192.168.1.8", 7000).lookup("Server");
        }
        catch(NotBoundException|RemoteException e) {
            e.printStackTrace(); // what happens *after* we reach this line?
        }
    }

    public String addMusic(String name,String genre,String length,String lyrics,String album) throws RemoteException {
        //server.search(album) <- Checks if album exists
        return server.addMusic(name,genre,length,lyrics,album);
    }
    public String addAlbum(String title, String releaseDate, String description, String artist) throws RemoteException {
        return server.addAlbum(title,releaseDate,description,artist);
    }
    public String addArtist(String name,String details,int solo) throws RemoteException {
        return server.addArtist(name,details, solo);
    }
    public String addReview(Review review,boolean isCreate) throws RemoteException {
        return server.addReview(review,isCreate);
    }


    public String updateMusic(Music music, String username) throws RemoteException {
        return server.updateMusic(music,username);
    }

    public String updateAlbum(Album album, String username) throws RemoteException {
        return server.updateAlbum(album,username);
    }
    public String updateArtist(Artist artist, String username) throws RemoteException {
        return server.updateArtist(artist,username);
    }



    public String deleteMusic(int id) throws RemoteException{
        return server.deleteMusic(id);
    }
    public String deleteAlbum(int id) throws RemoteException{
        return server.deleteAlbum(id);
    }
    public String deleteArtist(int id, ArrayList<Integer> arr) throws RemoteException{
        return server.deleteArtist(id,arr);
    }


    public ArrayList<Artist> searchArtist(String str) throws RemoteException{
        return server.searchArtist(str);
    }
    public ArrayList<Artist> searchArtist(int id) throws RemoteException{
        return server.searchArtist(id);
    }
    public ArrayList<Album> searchAlbum(int id) throws RemoteException{
        return server.searchAlbum(id);
    }

    public ArrayList<Album> searchAlbum(String str) throws RemoteException{
        return server.searchAlbum(str);
    }

    public ArrayList<Music> searchMusic(String str) throws RemoteException{
        return server.searchMusic(str);
    }

    public ArrayList<Music> searchMusic(int album_id) throws RemoteException{
        return server.searchMusic(album_id);
    }

    public ArrayList<Review> searchReview(int album_id) throws RemoteException{
        return server.searchReview(album_id);
    }
    public Review searchReview(String username,int album_id) throws RemoteException{
        return server.searchReview(username,album_id);
    }

}
