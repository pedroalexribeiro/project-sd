package web.action;

import shared.Album;
import shared.Artist;
import shared.Music;
import shared.User;
import web.model.MusicBean;
import web.model.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class Search extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String str;
    @Override
    public String execute() throws Exception{
        if(str != null && !str.equals("")) {


            ArrayList<Artist> artists = getMusicBean().searchArtist(str);
            if(artists!=null){
                session.put("searchArtists",artists);
            }

            ArrayList<Album> albums = getMusicBean().searchAlbum(str);
            if(albums!=null){
                session.put("searchAlbums",albums);
            }

            ArrayList<Music> musics = getMusicBean().searchMusic(str);
            if(musics!=null){
                session.put("searchMusics",musics);
            }

            return SUCCESS;
            }
        addActionError("Search Can´t be empty");
        return ERROR;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public MusicBean getMusicBean() {
        if(!session.containsKey("musicBean"))
            this.setMusicBean(new MusicBean());
        return (MusicBean) session.get("musicBean");
    }

    public void setMusicBean(MusicBean musicBean) {
        this.session.put("musicBean", musicBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
