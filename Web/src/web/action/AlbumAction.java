package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.*;
import web.model.MusicBean;

import java.util.ArrayList;
import java.util.Map;

public class AlbumAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private int id;
    private String title;
    private String releaseDate;
    private String description;
    private String artist;

    public String displayAlbum() throws Exception{
        int cont=0;
        Album a = new Album(this.title,this.releaseDate,this.description,this.artist,this.id);
        Artist ar = getMusicBean().searchArtist(Integer.parseInt(this.artist)).get(0);
        a.setArtistName(ar.name);
        ArrayList<Review> reviews = getMusicBean().searchReview(this.id);
        if(reviews != null){
            session.put("searchReviews",reviews);
            if(reviews.size()>0){
                for(Review r : reviews){
                    cont += r.getRating();
                }
                a.setAvgRating(cont / (reviews.size()));
            }
        }
        ArrayList<Music> music = getMusicBean().searchMusic(this.id);
        if(music != null){
            session.put("searchMusics",music);
        }

        /*Average Rating calculation*/
        session.put("searchAlbum",a);

        return SUCCESS;
    }

    public String editAlbum() throws Exception{
        if(this.title != null && this.releaseDate != null && this.description != null){
            Album album =  (Album)session.get("searchAlbum");
            Album a = new Album(this.title,this.releaseDate,this.description,album.artist,album.id);
            User user= (User) this.session.get("user");
            String answer = getMusicBean().updateAlbum(a,user.username);
            if(answer.equals("Success")) {
                session.put("searchAlbum",a);
                return SUCCESS;
            }
        }
        addActionError("There are Errors in this form");
        return ERROR;
    }

    public String deleteAlbum() throws Exception{
        Album album =  (Album)session.get("searchAlbum");
        getMusicBean().deleteAlbum(album.id);
        return SUCCESS;
    }



    public void setId(int id) {
        this.id = id;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public int getId() {
        return id;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle(){
        return title;
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
