package web.action;

import shared.Album;
import shared.Artist;
import shared.Music;
import shared.User;
import web.model.MusicBean;
import web.model.UserBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.util.*;

public class Search extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String str;
    @Override
    public String execute() throws Exception{
        if(str != null && !str.equals("")) {

            ArrayList<Artist> artists = getMusicBean().searchArtist(str);
            if(artists!=null){
                HashMap<Integer,Artist> hm = new HashMap<>();
                for(Artist a : artists){
                    hm.put(a.id,a);
                }
                artists.clear();
                artists.addAll(hm.values());
                session.put("searchArtists",artists);
            }

            ArrayList<Album> albums = getMusicBean().searchAlbum(str);
            if(albums!=null){
                HashMap<Integer,Album> hm = new HashMap<>();
                for(Album a:albums){
                    hm.put(a.id,a);
                }
                albums.clear();
                albums.addAll(hm.values());
                session.put("searchAlbums",albums);
            }


            ArrayList<Music> musics = getMusicBean().searchMusic(str);
            if(musics!=null){
                HashMap<Integer,Music> hm = new HashMap<>();
                for(Music m:musics){
                    hm.put(m.id,m);
                }
                musics.clear();
                musics.addAll(hm.values());
                session.put("searchMusics",musics);
            }


            ArrayList<User> users = getUserBean().searchUser(str);
            if(users!=null){
                session.put("searchUsers",users);
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

    public UserBean getUserBean() {
        if(!session.containsKey("userBean"))
            this.setUserBean(new UserBean());
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean) {
        this.session.put("userBean", userBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
