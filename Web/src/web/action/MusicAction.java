package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import rest.DropBoxRestClient;
import shared.File;
import shared.Music;
import shared.User;
import web.model.MusicBean;
import web.model.UserBean;

import java.util.ArrayList;
import java.util.Map;

public class MusicAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    public int id;
    public int album_id;
    public String name;
    public String type;
    public int length;
    public String lyrics;
    public String albumName;
    public ArrayList<File> files;

    public String displayMusic() throws Exception{
        Music m = new Music(this.name, this.type, this.length, this.lyrics, this.album_id, this.id);
        session.put("searchMusic",m);
        User user= (User) this.session.get("user");
        files = getUserBean().getDropboxFiles(user.username, id);
        for(File f : files){
            f.setTmpLink(getDropbox().playFile(f.id, getUser().dropbox_token));
        }
        /*Hyperlink to Album?*/
        return SUCCESS;
    }

    public String editMusic() throws Exception{
        if(this.name!=null && this.type!=null && this.lyrics!=null && !this.name.trim().equals("") && !this.type.trim().equals("") && !this.lyrics.trim().equals("")){
            Music music =  (Music)session.get("searchMusic");
            Music m = new Music(this.name, this.type, this.length, this.lyrics,music.album_id,music.id);
            User user= (User) this.session.get("user");
            if(getMusicBean().updateMusic(m,user.username).equals("Success")) {
                session.put("searchMusic",m);
                return SUCCESS;
            }
        }
        addActionError("There are Errors in this form");
        return ERROR;
    }

    public String deleteMusic() throws Exception{
        Music music =  (Music)session.get("searchMusic");
        getMusicBean().deleteMusic(music.id);
        return SUCCESS;
    }

    public String addMusic() throws Exception{
        if(this.name!=null && this.type!=null && this.lyrics!=null && this.albumName!=null &&this.length>0 && !this.name.trim().equals("") && !this.type.trim().equals("") && !this.lyrics.trim().equals("") && !this.albumName.trim().equals("")){
            String music = getMusicBean().addMusic(this.name, this.type, ""+this.length, this.lyrics, this.albumName);
            if(music.equalsIgnoreCase("Success")){
                return SUCCESS;
            }
        }
        addActionError("There are Errors in this form");
        return ERROR;
    }


    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public int getId() {
        return id;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public String getLyrics() {
        return lyrics;
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

    public UserBean getUserBean() {
        if(!session.containsKey("userBean"))
            this.setUserBean(new UserBean());
        return (UserBean) session.get("userBean");
    }

    public void setUserBean(UserBean userBean) {
        this.session.put("userBean", userBean);
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public DropBoxRestClient getDropbox() {
        return new DropBoxRestClient("http://localhost:8080/Web/login-dropbox");
    }

    public User getUser(){
        return (User) session.get("user");
    }
}
