import java.util.HashMap;
import java.util.Map;

public class UDP {
    private static final Map<String,String> user = new HashMap<>();
    private static final Map<String,String> notification = new HashMap<>();
    private static final Map<String,String> playlist = new HashMap<>();
    private static final Map<String,String> playlist_music = new HashMap<>();
    private static final Map<String,String> album = new HashMap<>();
    private static final Map<String,String> artist = new HashMap<>();
    private static final Map<String,String> music = new HashMap<>();
    private static final Map<String, Map<String,String>> hashmaps = new HashMap<>();
    static {
        user.put("username","string");
        user.put("password","string");
        user.put("email","string");
        user.put("name","string");
        user.put("personalinfo","string");
        user.put("edit","boolean");
        notification.put("notificationid","int");
        notification.put("text","string");
        notification.put("user_username","string");
        playlist.put("title","string");
        playlist.put("username","string");
        playlist_music.put("title_id", "int");
        playlist_music.put("music_id", "int");
        album.put("id","int");
        album.put("title","string");
        album.put("releasedate","string");
        album.put("description","string");
        album.put("artist_id","int");
        artist.put("id","int");
        artist.put("name","string");
        artist.put("details","string");
        music.put("id","int");
        music.put("name","string");
        music.put("genre","string");
        music.put("length","int");
        music.put("album_id","int");
        hashmaps.put("user", user);
        hashmaps.put("notification", notification);
        hashmaps.put("playlist", playlist);
        hashmaps.put("playlist_music", playlist_music);
        hashmaps.put("album", album);
        hashmaps.put("artist", artist);
        hashmaps.put("music", music);
    }

    public static String menuToSQL(Map<String, String> str){
        switch (str.get("function").toLowerCase()){
            case "create":
                System.out.println("create");
                return insertToSQL(str);
            case "delete":
                System.out.println("delete");
                return deleteToSQL(str);
            case "update":
                System.out.println("create");
                return updateToSQL(str);
            case "search":
                System.out.println("search");
                return searchToSQL(str);
        }
        return "";
    }

    private static String insertToSQL(Map<String, String> str){
        String output = "INSERT INTO " +str.get("what")+" VALUES (";
        Map<String,String> hash = hashmaps.get(str.get("what"));
        for (Map.Entry<String,String> entry : hash.entrySet()) {
            if(entry.getValue().equalsIgnoreCase("string")){
                output += "'" + str.get(entry.getKey()) + "', " ;
            }else{
                output += str.get(entry.getKey()) + "', ";
            }
        }
        output = output.substring(0, output.length() - 2);
        output += ");";
        return output;
    }

    private static String deleteToSQL(Map<String, String> str){
        return "DELETE FROM " + str.get("what") + " WHERE " + str.get("where") + ";";
    }

    private static String updateToSQL(Map<String, String> str){
        return "UPDATE " + str.get("what") + " SET " + str.get("set") + " WHERE " + str.get("where") + ";";
    }

    private static String searchToSQL(Map<String, String> str){
        return "SELECT * FROM " + str.get("what") + " WHERE " + str.get("where") + ";";
    }

    public static Map<String, String> protocolToHash(String packetInfo){
        //Translates Packets received from UDP to Arraylist for later conversion to  SQL
        Map<String, String> hash = new HashMap<>();
        String[] firstSplit = packetInfo.split(";");
        String[] secondSplit;
        for(int i=0; i < firstSplit.length; i++){
            secondSplit = firstSplit[i].split("\\|");
            hash.put(secondSplit[0], secondSplit[1]);
        }
        return hash;
    }




}


