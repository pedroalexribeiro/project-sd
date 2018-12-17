package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.*;
import web.model.MusicBean;

import java.util.ArrayList;
import java.util.Map;

public class ArtistAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    public String name;
    public String details;
    public int id;

    public String displayArtist() throws Exception{

        Artist artist = new Artist(this.name,this.details,false,this.id);
        session.put("searchArtist",artist);
        ArrayList<Album> albums = getMusicBean().searchAlbum(this.id);
        if(albums != null){
            session.put("searchAlbums",albums);
        }
        if (albums != null){
            ArrayList<Music> music = new ArrayList<>();
            for(Album a : albums){
                ArrayList<Music> temp = getMusicBean().searchMusic(a.getId());
                if(temp != null ) music.addAll(temp);
            }
            session.put("searchMusics",music);
        }
        return SUCCESS;
    }

    public String editArtist() throws Exception{
        if(this.name != null && this.details != null && !this.name.trim().equals("") && !this.details.trim().equals("")){
            Artist artist =  (Artist)session.get("searchArtist");
            Artist a = new Artist(this.name, this.details,true,artist.id);
            User user= (User) this.session.get("user");
            String answer = getMusicBean().updateArtist(a,user.username);
            if(answer.equals("Success")) {
                session.put("searchArtist",a);
                return SUCCESS;
            }
        }
        addActionError("There are Errors in this form");
        return ERROR;
    }

    public String deleteArtist() throws Exception{
        Artist artist =  (Artist)session.get("searchArtist");
        ArrayList<Integer> arr = new ArrayList<>();
        ArrayList<Album> album = getMusicBean().searchAlbum(artist.id);
        if(album.size() > 0){
            for(Album a : album){
                arr.add(a.getId());
            }
        }
        getMusicBean().deleteArtist(artist.id,arr);
        return SUCCESS;
    }

    public String addArtist() throws Exception{
        if(this.name != null && this.details != null && !this.name.trim().equals("") && !this.details.trim().equals("")){
            String artist = getMusicBean().addArtist(this.name, this.details, 1);
            if(artist.equalsIgnoreCase("Success")){
                return SUCCESS;
            }
        }
        addActionError("There are Errors in this form");
        return ERROR;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
