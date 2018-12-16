package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.*;
import web.model.MusicBean;

import java.util.ArrayList;
import java.util.Map;

public class ReviewAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String text;
    private int rating;
    private String EDIT = "edit";
    private String CREATE = "create";



    public String chooseReview() throws Exception{
        User user = (User) session.get("user");
        Album album = (Album) session.get("searchAlbum");
        Review r = getMusicBean().searchReview(user.username,album.id);
        if(r != null){
            session.put("searchReview",r);
            return EDIT;
        }
        return CREATE;
    }

    public String createReview() throws Exception{
        if(this.text != null && this.rating >0 && this.rating < 6){
            User user = (User) session.get("user");
            Album alb = (Album) session.get("searchAlbum");
            Review review = new Review(user.username,alb.id,this.text,this.rating, "now");
            String answer = getMusicBean().addReview(review,false);
            if(answer.equals("Success")) {
                return SUCCESS;
            }
        }
        addActionError("There are Errors in this form");
        return ERROR;
    }

    public String editReview() throws Exception{
        if(this.text != null && this.rating >0 && this.rating < 6){
            User user = (User) session.get("user");
            Album alb = (Album) session.get("searchAlbum");
            Review review = new Review(user.username,alb.id,this.text,this.rating,"now");
            String answer = getMusicBean().addReview(review,true);
            if(answer.equals("Success")) {
                session.put("searchReview",review);
                return SUCCESS;
            }
        }
        addActionError("There are Errors in this form");
        return ERROR;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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
