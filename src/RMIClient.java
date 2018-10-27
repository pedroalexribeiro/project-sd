import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RMIClient extends UnicastRemoteObject implements clientInterface {
    private static String rmi_ip;
    private static String my_ip;
    private static int my_port;

    public RMIClient() throws RemoteException {
        super();
        int randomNum = ThreadLocalRandom.current().nextInt(5000, 10000 + 1);
        my_port = randomNum;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10005 + randomNum);
            my_ip = socket.getLocalAddress().getHostAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws RemoteException {
        // argumentos da linha de comando: id do server
        if(args.length == 0){
            System.out.println("id of server needs to be an argument");
            System.exit(0);
        }
        rmi_ip = args[0];
        Network newNet = new Network();
        System.setProperty("java.rmi.server.hostname", newNet.getIP());
        run(false, "");
    }

    public void liveNotification(String note) throws RemoteException{
        System.out.println(note);
    }

    public static Boolean userStatus(User user){ // Checks if user is Online or not
        return (user==null);
    }

    public static Boolean stringContain(String s){
        return  (!s.contains("|") && !s.contains(";"));
    }

    public static String askInfo(String type,Scanner sc) {
        String output;
        while(true){
            System.out.println(type+": ");
            output = sc.nextLine();
            if(output.equalsIgnoreCase("n")){
                break;
            }else if(stringContain(output)){
                if(type.contains("yyyy-mm-dd")){
                    if(isValid(output)) break;
                    else continue;
                }
                return output;
            }
        }
        return "n";
    }

    public static int inBetween(int max,Scanner sc){
        try{
            String output=sc.nextLine();
            int choice = Integer.parseInt(output) - 1 ;
            if (Integer.parseInt(output) >= 1 && Integer.parseInt(output)<= max) return choice;

        }catch(NumberFormatException nfe){
            System.out.println("Try again->Invalid input");
            return (-1);
        }
        return (-1);
    }

    public static boolean isValid(String text) {
        if (text == null || !text.matches("\\d{4}-[01]\\d-[0-3]\\d"))
            return false;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        try {
            df.parse(text);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }


    public static void run(Boolean testing, String input) {

        System.getProperties().put("java.security.policy", "java.policy.applet");
        System.setSecurityManager(new RMISecurityManager());

        try {
            Scanner sc = new Scanner(System.in);
            boolean isRunning = true;
            Interface i;
            User user = null;

            while (isRunning) {
                if (!testing) {
                    System.out.println("\nInput:");
                    input = sc.nextLine();
                }
                i = (Interface) LocateRegistry.getRegistry(rmi_ip, 7000).lookup("Server");

                // Splits input into arraylist for latter use
                ArrayList<String> parts = new ArrayList<>();
                String s[] = input.split(" ");
                for (String element : s) {
                    parts.add(element);
                }

                switch (parts.get(0)) {
                    case "/helloworld": //Testing Message
                        String message = i.helloWorld();
                        System.out.println(message);
                        break;
                    case "/help":
                        System.out.println("Commands:" +
                                "\n/register user pass email name" +
                                "\n/login user pass" +
                                "\n/logout" +
                                "\n/add type" +
                                "\n/search type" +
                                "\n/editor user" +
                                "\n/upload" +
                                "\n/download" +

                                "\n/exit");
                        break;


                    case "/register":
                        if(parts.size() == 5 && !parts.get(4).matches(".*\\d+.*") && stringContain(input) && userStatus(user)){
                            String username=parts.get(1);
                            String pass=parts.get(2);
                            String email=parts.get(3);
                            String name=parts.get(4);

                            String answer = i.register(username,pass,email,name,false);
                            if(answer.equalsIgnoreCase("Error")){
                                System.out.println("Register Unsuccessful");
                            }else{
                                System.out.println("Register Successful");
                            }
                        }
                        break;


                    case "/login":
                        if (parts.size() == 3 && stringContain(input) ) {
                            if(user != null) {
                                System.out.println("You are Already Logged In");
                                break;
                            }
                            String username = parts.get(1);
                            String pass = parts.get(2);

                            user = i.login(username, pass);
                            if (user != null) {
                                try {
                                    clientInterface ci = new RMIClient();
                                    i.subscribe(username, ci); //Function that saves clientInterface and puts User.online true
                                    System.out.println("Log in Successful");

                                    //notifications
                                    ArrayList<Notification> notes = i.getNotifications(username);
                                    for(Notification note : notes){
                                        System.out.println(note.text);
                                    }
                                    i.clearDatabaseNotifications(username);
                                }catch(Exception e){
                                }
                            } else {
                                System.out.println("Login Unsuccessful");
                            }
                        }
                        break;

                    case "/logout":
                        if(user == null){
                            System.out.println("You are not logged in");
                        }
                        else{
                            i.logout(user.username);
                            user = null;
                            System.out.println("Bye Bye");
                        }
                        break;


                    case "/add":
                        if(parts.size() == 2 && !userStatus(user) && stringContain(input)){
                            if(user.editor){
                                switch(parts.get(1)){
                                    case "album":
                                            while(true){
                                                String releaseDate;
                                                System.out.println("Title:");
                                                String title = sc.nextLine();
                                                while(true) {
                                                    System.out.println("ReleaseDate (yyyy-mm-dd):");
                                                    releaseDate = sc.nextLine();
                                                    if(isValid(releaseDate)) break;
                                                }
                                                System.out.println("Description:");
                                                String description = sc.nextLine();
                                                System.out.println("Artist:");
                                                String artist = sc.nextLine();
                                                if(stringContain(title)&&stringContain(releaseDate)&&stringContain(description)&&stringContain(artist)){
                                                    System.out.println(i.addAlbum(title,releaseDate,description,artist));
                                                    break;
                                                }
                                            }
                                        break;
                                    case "artist":
                                        while(true){
                                            System.out.println("Name:");
                                            String name = sc.nextLine();
                                            System.out.println("Details:");
                                            String details = sc.nextLine();
                                            if(stringContain(name)&&stringContain(details)){
                                                System.out.println(i.addArtist(name,details));
                                                break;
                                            }
                                        }

                                        break;
                                    case "music":
                                        while(true){
                                            System.out.println("Name:");
                                            String name = sc.nextLine();
                                            System.out.println("Genre:");
                                            String genre = sc.nextLine();
                                            System.out.println("Length:");
                                            String length = sc.nextLine();
                                            System.out.println("Album:");
                                            String album = sc.nextLine();
                                            if(stringContain(name)&&stringContain(genre)&&stringContain(length)&&stringContain(album)){
                                                System.out.println(i.addMusic(name,genre,length,album));
                                                break;
                                            }
                                        }
                                        break;
                                }
                           }
                        }
                            break;

                    case "/editor": // Turns another user into an editor
                        if(parts.size() == 2 && !userStatus(user) && stringContain(input)){
                            String username = parts.get(1);
                            if(i.searchUser(username) == 1){
                                if(user.isEditor()) {
                                    Notification note = new Notification(username,"You are now an Editor");
                                    i.sendNotifcation(note, username);
                                }
                                else{
                                    System.out.println("Not an Editor/Chose another editor");
                                }
                            }else{
                                System.out.println("The username you wrote doesn't belong to anyone");
                            }
                        }
                        break;
                    case "/playlist":
                        break;

                    case "/delete":{
                        if(parts.size() == 2) {
                            String in;
                            while(true){
                                System.out.println("What:");
                                in = sc.nextLine();
                                if(stringContain(in)){
                                    break;
                                }
                            }
                            switch (parts.get(1)){
                                case "album":{
                                    ArrayList<Album> album = i.searchAlbum(in);
                                    int choice=0;
                                    if(album.size()>0) {
                                        if (album.size() > 1) {
                                            while (true) {
                                                for (int j = 0; j < album.size(); j++) {
                                                    System.out.println("title: " + album.get(j).title + " releasedate: " + album.get(j).releaseDate + " description: " + album.get(j).description);
                                                }
                                                System.out.println("Chose from 1-" + album.size());
                                                choice = inBetween(album.size(), sc);
                                                if (choice != -1) {
                                                    break;
                                                }
                                            }
                                        }
                                        System.out.println("You sure you want to delete that album [y/N]");
                                        in = sc.nextLine();
                                        if (in.equalsIgnoreCase("y")) {
                                            i.deleteAlbum(album.get(choice).id);
                                        }
                                        break;
                                    }
                                    break;
                                }
                                case "artist":{
                                    ArrayList<Artist> artists = i.searchArtist(in);
                                    int choice=0;
                                    if(artists.size()>0) {
                                        if (artists.size() > 1) {
                                            while (true) {
                                                for (int j = 0; j < artists.size(); j++) {
                                                    System.out.println("name: " + artists.get(j).name + " details: " + artists.get(j).details);
                                                }
                                                System.out.println("Chose from 1-" + artists.size());
                                                choice = inBetween(artists.size(), sc);
                                                if (choice != -1){
                                                    break;
                                                }
                                            }
                                        }
                                        System.out.println("You sure you want to delete that album [y/N]");
                                        in = sc.nextLine();
                                        if (in.equalsIgnoreCase("y")) {
                                            i.deleteArtist(artists.get(choice).id);
                                        }
                                        break;
                                    }
                                    break;
                                }
                                case "music": {
                                    ArrayList<Music> music = i.searchMusic(in);
                                    int choice=0;
                                    if(music.size()>0) {
                                        while (true) {
                                            for (int j = 0; j < music.size(); j++) {
                                                System.out.println("name: " + music.get(j).name);
                                                System.out.println("genre: " + music.get(j).type);
                                                System.out.println("length: " + music.get(j).length);
                                            }
                                            if (music.size() > 1) {
                                                System.out.println("Chose from 1-" + music.size());
                                                choice = inBetween(music.size(), sc);
                                                if (choice != -1){
                                                    break;
                                                }
                                            } else {
                                                break;
                                            }
                                        }
                                        System.out.println("You sure you want to delete that album [y/N]");
                                        in = sc.nextLine();
                                        if(in.equalsIgnoreCase("y")){
                                            i.deleteMusic(music.get(choice).id);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case "/search":
                        if (parts.size() == 2 ) {
                            String iput;
                            while(true){
                                System.out.println("What:");
                                iput = sc.nextLine();
                                if(stringContain(iput)){
                                    break;
                                }
                            }
                            switch (parts.get(1)){
                                case "album":
                                    ArrayList<Album> album = i.searchAlbum(iput);

                                    int choice=0;
                                    if(album.size()>0) {
                                        if(album.size()>1){
                                            while(true){
                                                for (int j=0; j<album.size(); j++){
                                                    System.out.println("title: " + album.get(j).title+" releasedate: " + album.get(j).releaseDate+" description: " + album.get(j).description);
                                                }
                                                System.out.println("Chose from 1-"+album.size());
                                                choice = inBetween(album.size(),sc);
                                                if(choice != -1)break;
                                            }
                                        }
                                        ArrayList<Review> reviews = i.searchReview(album.get(choice).getId());
                                        ArrayList<Music> musics = i.searchMusic(album.get(choice).getId());
                                        int count = 0,avgRating=0;

                                        if(reviews.size()>0) {
                                            for (Review r : reviews) {
                                                count += r.getRating();
                                            }
                                            avgRating = count/reviews.size();
                                        }
                                        System.out.println(album.get(choice).toString() +" Rating:"+ avgRating);

                                        System.out.println("\nMusics:");
                                        for(Music m : musics){
                                            System.out.println(m.toString());
                                        }
                                        System.out.println("\nReviews:");
                                        for(Review r : reviews){
                                            System.out.println(r.toString());
                                        }

                                        while(true){
                                            System.out.println("/review\n/edit\n/exit");
                                            String str = sc.nextLine();
                                            if(str.equalsIgnoreCase("/review")){
                                                Boolean isCreated = false;
                                                Boolean flag = false;
                                                String txt;
                                                int score;
                                                Review r = i.searchReview(user.username,album.get(choice).getId());
                                                if(r != null){
                                                    System.out.println("Review already Exists, change it? y/n");
                                                    while(true){
                                                        String x = sc.nextLine();
                                                        if(x.equalsIgnoreCase("n")) {
                                                            flag = false;
                                                            break;
                                                        }
                                                        else if(x.equalsIgnoreCase("y")){
                                                            flag=true;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if(r==null || flag){
                                                    while (true) {
                                                        System.out.println("Score (1-5)");
                                                        try {
                                                            score = Integer.parseInt(sc.nextLine());
                                                            System.out.println("Review");
                                                            txt = sc.nextLine();
                                                            if (txt.length() < 300 && stringContain(txt) && score > 0 && score < 6) {
                                                               isCreated = true;
                                                                break;
                                                            }
                                                        } catch (NumberFormatException nfe) {
                                                            System.out.println("Input valid Score");
                                                        }
                                                    }
                                                    Review review = new Review(user.username, album.get(choice).getId(), txt, score ,"now");
                                                    i.addReview(review,isCreated);
                                                    break;
                                                }
                                            }

                                            else if(str.equalsIgnoreCase("/edit") && !userStatus(user)){
                                                if(user.editor) {
                                                    boolean changedSmth = false;
                                                    System.out.println("Want to Change:\n n ->Doesnt Change\n Write Anything else -> Changes");
                                                    Album a = album.get(choice);

                                                    String answer = askInfo("title", sc);
                                                    if (!answer.equals("n")) {
                                                        a.title = answer;
                                                        changedSmth = true;
                                                    }

                                                    answer = askInfo("release Date (yyyy-mm-dd) : ", sc);
                                                    if (!answer.equals("n")) {
                                                        a.releaseDate = answer;
                                                        changedSmth = true;
                                                    }

                                                    answer = askInfo("Description: ", sc);
                                                    if (!answer.equals("n")) {
                                                        a.description = answer;
                                                        changedSmth = true;
                                                    }

                                                    if (changedSmth) {
                                                        answer = i.updateAlbum(a, user.username);
                                                        System.out.println(answer);
                                                    }
                                                }else{
                                                    System.out.println("You don't have edit rights");
                                                }
                                            }
                                            else if(str.equalsIgnoreCase("/exit")){break;}
                                        }
                                    }else{
                                        System.out.println("Nothing found");
                                    }

                                    break;



                                case "music":
                                    ArrayList<Music> music = i.searchMusic(iput);
                                    choice=0;
                                    if(music.size()>0){
                                    while(true){
                                        for (int j=0; j<music.size(); j++){
                                            System.out.println("name: " + music.get(j).name);
                                            System.out.println("genre: " + music.get(j).type);
                                            System.out.println("length: " + music.get(j).length);
                                        }
                                        if(music.size()>1){
                                            System.out.println("Chose from 1-"+music.size());
                                            choice = inBetween(music.size(),sc);
                                            if(choice != -1)break;
                                        }
                                        else{
                                            break;
                                            }
                                        }
                                        System.out.println("Music:\nname: " + music.get(choice).name);
                                        System.out.println("genre: " + music.get(choice).type);
                                        System.out.println("length: " + music.get(choice).length);



                                        while(true){
                                            System.out.println("/edit \n /exit");
                                            String str = sc.nextLine();
                                            if(str.equalsIgnoreCase("/edit") && !userStatus(user)){
                                                if(user.editor) {
                                                    boolean changedSmth = false;
                                                    System.out.println("Want to Change:\n n ->Doesnt Change\n Write Anything else -> Changes");
                                                    Music a = music.get(choice);

                                                    String answer = askInfo("Name", sc);
                                                    if (!answer.equals("n")){
                                                        a.name = answer;
                                                        changedSmth = true;
                                                    }

                                                    answer = askInfo("genre : ", sc);
                                                    if (!answer.equals("n")){
                                                        a.type = answer;
                                                        changedSmth = true;
                                                    }

                                                    answer = askInfo("length : ", sc);
                                                    while (true) {
                                                        System.out.println("length: ");
                                                        String output = sc.nextLine();
                                                        if (output.equalsIgnoreCase("n")) {
                                                            break;
                                                        } else {
                                                            changedSmth = true;
                                                            try {
                                                                int len = Integer.parseInt(output);
                                                                if (len > 0 && len < 2000) break;
                                                            } catch (NumberFormatException nfe) {
                                                                System.out.println("Try a valid number");
                                                            }
                                                        }
                                                    }
                                                    if(changedSmth){
                                                        answer = i.updateMusic(a, user.username);
                                                        System.out.println(answer);
                                                    }
                                                }else{
                                                    System.out.println("You don't have edit rights");
                                                }
                                            }
                                            else if(str.equalsIgnoreCase("/exit")){break;}
                                        }
                                    }else{
                                        System.out.println("Not found");
                                    }
                                    break;




                                case "artist":
                                    ArrayList<Artist> artists = i.searchArtist(iput);
                                    choice=0;
                                    if(artists.size()>0) {
                                        if(artists.size()>1){
                                            while(true){
                                                for (int j=0; j<artists.size(); j++){
                                                    System.out.println("name: " + artists.get(j).name+" details: " + artists.get(j).details);
                                                }
                                                System.out.println("Chose from 1-"+artists.size());
                                                choice = inBetween(artists.size(),sc);
                                                if(choice != -1)break;
                                            }
                                        }
                                        System.out.println(artists.get(choice).toString());
                                        ArrayList<Album> albums = i.searchAlbum(artists.get(choice).name);
                                        for(Album a : albums){
                                            System.out.println(a.toString());
                                            ArrayList<Music> musics = i.searchMusic(a.getId());
                                            for(Music m : musics){
                                                System.out.println("     "+m.toString());
                                            }
                                        }


                                        while(true){
                                            System.out.println("/edit \n /exit");
                                            String str = sc.nextLine();
                                            if(str.equalsIgnoreCase("/edit") && !userStatus(user)){
                                                if(user.editor) {
                                                    boolean changedSmth = false;
                                                    System.out.println("Want to Change:\n n ->Doesnt Change\n Write Anything else -> Changes");
                                                    Artist a = artists.get(choice);

                                                    String answer = askInfo("Name", sc);
                                                    if (!answer.equals("n")){
                                                        a.name = answer;
                                                        changedSmth = true;
                                                    }

                                                    answer = askInfo("Details : ", sc);
                                                    if (!answer.equals("n")){
                                                        a.details = answer;
                                                        changedSmth = true;
                                                    }

                                                    if(changedSmth){
                                                        answer = i.updateArtist(a, user.username);
                                                        System.out.println(answer);
                                                    }
                                                }else{
                                                    System.out.println("You don't have edit rights");
                                                }
                                            }
                                            else if(str.equalsIgnoreCase("/exit")){break;}
                                        }
                                    }else{
                                        System.out.println("Nothing found");
                                    }
                                    break;
                                default:
                                    System.out.println("Not an available option");
                            }
                        }
                        break;



                    case "/upload": {
                        System.out.println("Music:");
                        String musicName = sc.nextLine();
                        ArrayList<Music> musicList = i.searchMusic(musicName);
                        if (musicList.size() == 0) {
                            System.out.println("No music found with that name");
                            break;
                        }
                        int index = 0;
                        if(musicList.size() > 1){
                            for(int j = 0; j<musicList.size(); j++){
                                System.out.println("name: " + musicList.get(j).name);
                                System.out.println("genre: " + musicList.get(j).type);
                                System.out.println("length: " + musicList.get(j).length);
                            }
                            System.out.println("Choose one from 1 to" + musicList.size());
                            do{
                                String oput =sc.nextLine();
                                index = Integer.parseInt(oput) - 1 ;
                            }while(index < 0 || index > musicList.size()-1 );
                        }
                        System.out.println("Filepath:");
                        String filepath = sc.nextLine();
                        File soundFile = new File(filepath);
                        if (!soundFile.exists() || !soundFile.isFile()) {
                            System.out.println("not a file: " + soundFile);
                            break;
                        }
                        String smth = i.askIP();
                        String arr[] = smth.split("\\|");
                        Socket sck = new Socket(arr[0], Integer.parseInt(arr[1]));
                        String fileInfo = "username|" + user.username + ";music_name|" + musicList.get(index).name + ";music_id|" + musicList.get(index).id;
                        DataOutputStream outInfo = new DataOutputStream(sck.getOutputStream());
                        outInfo.writeUTF(fileInfo);
                        byte[] buffer = Files.readAllBytes(soundFile.toPath());
                        try {
                            OutputStream out = sck.getOutputStream();
                            out.write(buffer, 0, buffer.length);
                        } finally {
                            sck.close();
                        }
                        System.out.println("Successful");
                        break;
                    }
                    case "/download": {
                        System.out.println("Music:");
                        String musicName = sc.nextLine();
                        ArrayList<Music> musicList = i.searchMusic(musicName);
                        if (musicList.size() == 0) {
                            System.out.println("No music found with that name");
                            break;
                        }
                        int index = 0;
                        if(musicList.size() > 1){
                            for(int j = 0; j<musicList.size(); j++){
                                System.out.println("name: " + musicList.get(j).name);
                                System.out.println("genre: " + musicList.get(j).type);
                                System.out.println("length: " + musicList.get(j).length);
                            }
                            System.out.println("Choose one from 1 to" + musicList.size());
                            do{
                                String oput =sc.nextLine();
                                index = Integer.parseInt(oput) - 1 ;
                            }while(index < 0 || index > musicList.size()-1 );
                        }
                        System.out.println("download starting....");
                        DownloadFile thread = new DownloadFile(my_port, "downloads" + File.separator + musicList.get(index).name + ".mp3");
                        thread.start();
                        String status = i.downloadFile(user.username, musicList.get(index).id, my_ip, my_port);
                        System.out.println(status);
                        break;
                    }
                    case "/share": {
                        System.out.println("Music:");
                        String name = sc.nextLine();
                        ArrayList<Music> list = i.searchMusic(name);
                        if (list.size() == 0) {
                            System.out.println("No music found with that name");
                            break;
                        }
                        int index = 0;
                        if(list.size() > 1){
                            for(int j = 0; j<list.size(); j++){
                                System.out.println("name: " + list.get(j).name);
                                System.out.println("genre: " + list.get(j).type);
                                System.out.println("length: " + list.get(j).length);
                            }
                            System.out.println("Choose one from 1 to" + list.size());
                            do{
                                String oput =sc.nextLine();
                                index = Integer.parseInt(oput) - 1 ;
                            }while(index < 1 || index > list.size());
                        }
                        int file_id = i.searchFile(user.username, list.get(index).id);
                        if(file_id == -1){
                            System.out.println("You have no file uploaded to that music");
                            break;
                        }
                        System.out.println("With: ");
                        String username = sc.nextLine();
                        if(i.searchUser(username) < 0){
                            System.out.println("Enter a valid username");
                            break;
                        }
                        i.shareFile(username, list.get(index).id, file_id);
                        System.out.println("Successful");
                        break;
                    }
                    case "/exit":
                        isRunning = false;
                        break;
                    default:
                        break;
                }

                /*
                 * if it gets here, reset Testing <-- means client isnt waiting for
                 * server(connection didnt catch an exception)
                 */
                testing = false;
            }
            System.out.println("Bye Bye");
        } catch (Exception e) {
            try {
                e.printStackTrace();
                TimeUnit.SECONDS.sleep(1); // sleep for a bit
                e.printStackTrace();
                //run(true, input);
            } catch (InterruptedException ie) {
                System.out.println(ie);
            }
        }
    }
}