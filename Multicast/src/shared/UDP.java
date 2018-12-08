package shared;

import java.util.HashMap;
import java.util.Map;

public class UDP {
    private static final Map<String,String> user = new HashMap<>();
    private static final Map<String,String> notification = new HashMap<>();
    private static final Map<String,String> playlist = new HashMap<>();
    private static final Map<String,String> music_playlist = new HashMap<>();
    private static final Map<String,String> album = new HashMap<>();
    private static final Map<String,String> artist = new HashMap<>();
    private static final Map<String,String> music = new HashMap<>();
    private static final Map<String,String> file = new HashMap<>();
    private static final Map<String,String> review = new HashMap<>();
    private static final Map<String,String> file_user = new HashMap<>();
    private static final Map<String,String> user_editor_album = new HashMap<>();
    private static final Map<String,String> user_editor_artist = new HashMap<>();
    private static final Map<String,String> user_editor_music = new HashMap<>();
    private static final Map<String,String> composed = new HashMap<>();
    private static final Map<String,String> featured = new HashMap<>();
    private static final Map<String,String> wrote_lyrics = new HashMap<>();
    private static final Map<String,String> role = new HashMap<>();
    private static final Map<String, Map<String,String>> hashmaps = new HashMap<>();
    static {
        user.put("username","string");
        user.put("password","string");
        user.put("email","string");
        user.put("name","string");
        user.put("personal_info","string");
        user.put("editor","boolean");
        notification.put("id","int");
        notification.put("text","string");
        notification.put("user_username","string");
        playlist.put("id", "int");
        playlist.put("name","string");
        playlist.put("user_username","string");
        music_playlist.put("playlist_id", "int");
        music_playlist.put("music_id", "int");
        album.put("id","int");
        album.put("title","string");
        album.put("release_date","string");
        album.put("description","string");
        album.put("artist_id","int");
        artist.put("id","int");
        artist.put("name","string");
        artist.put("details","string");
        artist.put("solo", "boolean");
        music.put("id","int");
        music.put("name","string");
        music.put("genre","string");
        music.put("length","int");
        music.put("lyrics", "string");
        music.put("album_id","int");
        file.put("filepath", "string");
        file.put("user_username", "string");
        file.put("music_id", "int");
        review.put("text", "string");
        review.put("rating","int");
        review.put("datee", "date");
        review.put("album_id", "int");
        review.put("user_username", "string");
        file_user.put("file_user_username", "string");
        file_user.put("user_username", "string");
        file_user.put("file_music_id", "int");
        user_editor_album.put("user_username", "string");
        user_editor_album.put("album_id", "int");
        user_editor_artist.put("user_username", "string");
        user_editor_artist.put("artist_id", "string");
        user_editor_music.put("user_username", "string");
        user_editor_music.put("music_id", "string");
        composed.put("artist_id", "int");
        composed.put("music_id", "int");
        featured.put("artist_id", "int");
        featured.put("music_id", "int");
        wrote_lyrics.put("artist_id", "int");
        wrote_lyrics.put("music_id", "int");
        role.put("role", "string");
        role.put("artist_id", "int");
        role.put("group_id", "int");
        hashmaps.put("user", user);
        hashmaps.put("notification", notification);
        hashmaps.put("playlist", playlist);
        hashmaps.put("music_playlist", music_playlist);
        hashmaps.put("album", album);
        hashmaps.put("artist", artist);
        hashmaps.put("music", music);
        hashmaps.put("file", file);
        hashmaps.put("review", review);
        hashmaps.put("user_editor_album", user_editor_album);
        hashmaps.put("user_editor_artist", user_editor_artist);
        hashmaps.put("user_editor_music", user_editor_music);
        hashmaps.put("file_user", file_user);
        hashmaps.put("composed", composed);
        hashmaps.put("featured", featured);
        hashmaps.put("wrote_lyrics", wrote_lyrics);
        hashmaps.put("role", role);
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
                System.out.println("update");
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


