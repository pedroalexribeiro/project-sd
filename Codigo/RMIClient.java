import java.rmi.Naming;
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

    public static void run(Boolean testing, String input) {
        try {
            Scanner sc = new Scanner(System.in);
            boolean isRunning = true;
            Interface i;
            User user = null;

            while (isRunning) {
                if (!testing) {
                    System.out.println("/helloworld -> \n/login user pass -> \n/register user pass email name ->");
                    System .out.println("\nInput: ");
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
                    case "/helloworld":
                        String message = i.helloWorld();
                        System.out.println(message);
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
                                    i.subscribe(username,rc);
                                }catch(Exception e){}
                                System.out.println("Log in Successful");
                            }
                            else{
                                System.out.println("Log in  Unsuccessful");
                            }
                        }
                        break;


                    case "/note": // Testing Notifications
                        if(parts.size() == 3 && user != null){
                            String username = parts.get(1);
                            String text = parts.get(2);

                            Notification note = new Notification(username,text);
                            i.sendNotifcation(note,username);
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