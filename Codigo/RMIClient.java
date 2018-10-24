import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class RMIClient extends UnicastRemoteObject implements clientInterface {
    public RMIClient() throws RemoteException {
        super();
    }
    public static void main(String args[]) throws RemoteException {
        run(false, "");
    }

    public void liveNotification(Notification note) throws RemoteException{
        System.out.println(note.text);
    }

    public static Boolean userStatus(User user){ // Checks if user is Online or not
        return (user==null);
    }

    public static Boolean stringContain(String s){
        return  (!s.contains("|") && !s.contains(";"));
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
                    i = (Interface) LocateRegistry.getRegistry("192.168.1.11",7000).lookup("Server");

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
                                "\n/helloworld" +
                                "\n/register user pass email name" +
                                "\n/login user pass" +
                                "\n/logout" +
                                "\n/add type" +

                                "\n/editor user" +
                                "\n/playlist method title music" +
                                "\n/search type text" +
                                "\n/exit");
                        break;


                    case "/register":
                        if(parts.size() == 5 && !parts.get(4).matches(".*\\d+.*") && stringContain(input)){
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

                            user = i.login(username,pass);
                            if(user != null){
                                try{
                                    RMIClient rc = new RMIClient();
                                    i.subscribe(username,rc); //Function that saves clientInterface and puts User.online true
                                    System.out.println("Log in Successful");

                                    //notifications
                                    ArrayList<Notification> notes = i.getNotifications(username);
                                    for(Notification note : notes){
                                        System.out.println(note.text);
                                    }
                                    i.clearDatabaseNotifications(username);
                                }catch(Exception e){
                                }
                            }
                            else{
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
                                                System.out.println("Title:");
                                                String title = sc.nextLine();
                                                System.out.println("ReleaseDate:");
                                                String releaseDate = sc.nextLine();
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
                            if(user.isEditor()) {
                                Notification note = new Notification(username,"You are now an Editor");
                                i.sendNotifcation(note, username);
                            }
                            else{
                                System.out.println("Not an Editor/Chose another editor");
                            }
                        }
                        break;


                    case "/playlist":
                        if(!userStatus(user)){
                            if (2<parts.size() && parts.size()<5){
                                String method = parts.get(1);
                                String title = parts.get(2);
                                String music = "";
                                if(parts.size()==4 && ( method.equalsIgnoreCase("add")||method.equalsIgnoreCase("remove"))){
                                    music = parts.get(3);
                                }
                                String output = i.playlistMethods(method,title,music,user.username);
                                System.out.println(output);
                            }
                        }
                        break;

                    case "/Search":
                        if(parts.size() == 3 && !userStatus(user)){
                            String whereSearch = parts.get(1);
                            String word = parts.get(2);

                            ArrayList<String> options = i.search(word,whereSearch);

                            System.out.println(options + "\n/exit "+"\nChoose One");
                            String input2 = sc.nextLine();
                            while(true){
                                if(input2.equalsIgnoreCase("/exit")){
                                    break;
                                }
                               for(int j=0;j<options.size();j++){
                                   if(input2.equalsIgnoreCase(options.get(j))){


                                   }
                               }

                               while(true){ //Another Switch?F
                                    break;
                               }


                                System.out.println("Try Again");
                            }
                        }
                        break;





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
                TimeUnit.SECONDS.sleep(1); // sleep for a bit
                e.printStackTrace();
                //run(true, input);
            } catch (InterruptedException ie) {
                System.out.println(ie);
            }
        }
    }
}