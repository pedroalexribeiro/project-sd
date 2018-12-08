package web.model;

import rmiserver.Interface;
import shared.Album;
import shared.Artist;
import shared.Music;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class MusicBean {
    private Interface server;

    public MusicBean() {
        try {
            server = (Interface) LocateRegistry.getRegistry("192.84.13.39", 7000).lookup("Server");
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



    public String updateMuic(Music music, String username) throws RemoteException {
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
}
