import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
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

    public static void run(Boolean testing, String input) {
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
                i = (Interface) LocateRegistry.getRegistry(7000).lookup("Server");

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
                                "\n/login user pass" +
                                "\n/register user pass email name" +
                                "\n/editor user" +
                                "\n/playlist method title music" +
                                "\n/search type text" +
                                "\n/exit");
                        break;


                    case "/register":
                        if(parts.size() == 5 && !parts.get(4).matches(".*\\d+.*")){
                            String username=parts.get(1);
                            String pass=parts.get(2);
                            String email=parts.get(3);
                            String name=parts.get(4);

                            user = i.register(username,pass,email,name,false);
                            if(user != null){
                                System.out.println("Register Successful");
                            }else{
                                System.out.println("Register Unsuccessful");
                            }
                        }
                        break;



                    case "/login":
                        if (parts.size() == 3) {
                            String username = parts.get(1);
                            String pass = parts.get(2);

                            user = i.login(username,pass);
                            if(user != null){
                                try{
                                    RMIClient rc = new RMIClient();
                                    i.subscribe(username,rc); //Function that saves clientInterface and puts User.online true
                                    System.out.println("Log in Successful");

                                    //notifications
                                    for(Notification tempNote : user.getNotifications()){
                                        System.out.println("Notification: " + tempNote.text);
                                        i.clearDatabaseNotifications(user);
                                    }

                                }catch(Exception e){
                                }
                            }
                            else{
                                System.out.println("Log in  Unsuccessful");
                            }
                        }
                        break;


                    case "/editor": // Turns another user into an editor
                        if(parts.size() == 2 && !userStatus(user)){
                            String username = parts.get(1);
                            if(user.isEditor() && user.username.equalsIgnoreCase(username)) {
                                Notification note = new Notification(username,"You are now an Editor");
                                i.sendNotifcation(note, username,true);
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
                                String output = i.playlistMethods(method,title,music,user);
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

                                       i.retrieveInformation(whereSearch,options.get(j));

                                   }
                               }

                               while(true){ //Another Switch?
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
                run(true, input);
            } catch (InterruptedException ie) {
                System.out.println(ie);
            }
        }
    }
}