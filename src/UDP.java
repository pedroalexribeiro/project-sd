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
    private static final Map<String,String> file = new HashMap<>();
    private static final Map<String,String> user_file = new HashMap<>();
    private static final Map<String, Map<String,String>> hashmaps = new HashMap<>();
    static {
        user.put("username","string");
        user.put("password","string");
        user.put("email","string");
        user.put("name","string");
        user.put("personalinfo","string");
        user.put("editor","boolean");
        notification.put("id","int");
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
        file.put("filepath", "string");
        file.put("user_username", "string");
        file.put("music_id", "int");
        user_file.put("file_id", "int");
        user_file.put("user_username", "string");
        user_file.put("music_id", "int");
        hashmaps.put("user", user);
        hashmaps.put("notification", notification);
        hashmaps.put("playlist", playlist);
        hashmaps.put("playlist_music", playlist_music);
        hashmaps.put("album", album);
        hashmaps.put("artist", artist);
        hashmaps.put("music", music);
        hashmaps.put("file", file);
        hashmaps.put("user_file", user_file);
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
        String column_names = "(";
        String values = "(";
        Map<String,String> hash = hashmaps.get(str.get("what"));
        for (Map.Entry<String,String> entry : hash.entrySet()) {
            if(entry.getValue().equalsIgnoreCase("string")){
                values += "'" + str.get(entry.getKey()) + "', " ;
            }else{
                values += str.get(entry.getKey()) + ", ";
            }
            column_names += entry.getKey() + ", ";
        }
        column_names = column_names.substring(0, column_names.length() - 2);
        values = values.substring(0, values.length() - 2);
        column_names += ")";
        values += ")";
        String output = "INSERT INTO " +str.get("what")+ " " + column_names + " VALUES " + values + ";";
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
            hash.put(secondSplit[0].trim(), secondSplit[1].trim());
        }
        return hash;
    }




}


