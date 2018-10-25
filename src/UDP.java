import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UDP {
    private static final Map<String,String> strType = new HashMap<>() ;
    static {
        strType.put("title","string");
        strType.put("releasedate","string");
        strType.put("description","string");
        strType.put("name","string");
        strType.put("details","string");
        strType.put("filepath","string");
        strType.put("username","string");
        strType.put("genre","string");
        strType.put("text","string");
        strType.put("password","string");
        strType.put("email","string");
        strType.put("personalinfo","string");
        strType.put("datee","string");
        strType.put("notification","string");
        strType.put("notificationid","null");
        strType.put("user_username","string");
        strType.put("length","int");
        strType.put("rating","int");
        strType.put("id","int");
        strType.put("edit","boolean");
        strType.put("editor","boolean");
    }



    public static String menuToSQL(ArrayList<String> str){
        switch (str.get(0).toLowerCase()){
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

    private static String insertToSQL(ArrayList<String> str){
        int i=2;
        String output = "INSERT INTO " +str.get(1)+" VALUES (";
        for( ; i < str.size() ; i+=2){
            if(strType.get(str.get(i).toLowerCase()).equalsIgnoreCase("string")){
                output += "'"+str.get(i+1)+"'" + ", ";
            }
            else{
                output += str.get(i+1) + ", ";
            }
        }
        output = output.substring(0, output.length() - 2);
        output += ");";
        System.out.println(output);
        return output;
    }

    private static String deleteToSQL(ArrayList<String> str){
        String output;
        if(strType.get(str.get(2)).equalsIgnoreCase("String")){
            output = "DELETE FROM " + str.get(1) + " WHERE "+str.get(2)+" = " +"'"+ str.get(3)+"'"+";";
        }else{
            output = "DELETE FROM " + str.get(1) + " WHERE "+str.get(2)+" = " +str.get(3)+";";
        }
        return output;
    }

    private static String updateToSQL(ArrayList<String> str){
        int i = 2;
        String output = "UPDATE " + str.get(1) + " SET ";
        for(; i < str.size(); i += 2){
            if(str.get(i).equalsIgnoreCase("WHERE")){
                i++;
                break;
            }
            if(strType.get(str.get(i)).equalsIgnoreCase("string")){
                output += str.get(i) + "=" + "'"+str.get(i+1)+"'"+ ", ";
            }
            else{
                output += str.get(i) + "=" + str.get(i+1) + ", ";
            }
        }
        output = output.substring(0, output.length() - 2);
        output += " WHERE ";
        for(; i < str.size(); i += 2){
            if(strType.get(str.get(i)).equalsIgnoreCase("string")){
                output += str.get(i) + "=" + "'"+str.get(i+1)+"'" + ", ";
            }
            else{
                output += str.get(i) + "=" + str.get(i+1) + ", ";
            }
        }
        output = output.substring(0, output.length() - 2);

        System.out.println(output);
        return output;
    }

    private static String searchToSQL(ArrayList<String> str){
        int i = 2;
        String output = "SELECT * FROM " + str.get(1) + " WHERE ";
        for(; i < str.size(); i += 2){
            if(strType.get(str.get(i)).equalsIgnoreCase("string")){
                output += str.get(i) + "=" + "'"+str.get(i+1)+"'" + " AND ";
            }
            else{
                output += str.get(i)+ "=" + str.get(i+1) + " AND ";
            }
        }
        output = output.substring(0, output.length() - 5);
        output += ";";
        return output;
    }

    public static ArrayList<String> packetToArr(String packet){
        //Translates Packets received from UDP to Arraylist for later conversion to  SQL
        String word = "";
        ArrayList<String> packetToUDP = new ArrayList<>();
        for(int i=0;i<packet.length();i++){
            if(packet.charAt(i) == '|' || packet.charAt(i) == ';'){
                word = word.substring(0,word.length()-1);
                packetToUDP.add(word);
                word = "";
                i++;
            }else{
                word += packet.charAt(i);
            }
        }
        return packetToUDP;
    }




}

