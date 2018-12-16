package web.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import web.model.MusicBean;
import java.util.Map;


public class AddArtist extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String name = null, details = null;
    private boolean solo = false;
    private int isSolo = 0;

    public String execute() throws Exception{
        String artist = getMusicBean().addArtist(this.name, this.details, this.isSolo);
        if(!artist.equalsIgnoreCase("error")){
            return SUCCESS;
        }
        return ERROR;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isSolo() {
        return solo;
    }

    public void setSolo(boolean solo) {
        if(solo){
            isSolo = 1;
        }
        this.solo = solo;
    }
}
