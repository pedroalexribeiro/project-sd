import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.sql.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.ArrayList;

public class MulticastServer {
    private static String MULTICAST_ADDRESS = "224.1.1.1";
    private static int MULTICAST_PORT = 4100;
    private static int RMI_PORT = 4000;
    private int id;

    public static void main(String[] args) {
        // argumentos da linha de comando: id do server
        /*if(args.length == 0){
            System.out.println("id of server needs to be an argument");
            System.exit(0);
        }*/
        new MulticastServer();
    }

    public MulticastServer(){
        this.id = 1;
        MulticastSocket receiveSocket = null;
        MulticastSocket senderSocket = null;
        int numThreads = 0;
        System.out.println("Starting...");
        try {
            receiveSocket = new MulticastSocket(MULTICAST_PORT);  // create socket and bind it
            senderSocket = new MulticastSocket();  // create socket and doesnt bind it cause only for sending
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            receiveSocket.joinGroup(group);
            while (true) {
                // waits for packet from multicast
                byte[] buffer = new byte[1000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                receiveSocket.receive(packet);
                // creates thread to handle the work
                HandleWork thread = new HandleWork(packet, senderSocket);
                thread.start();
                numThreads++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            receiveSocket.close();
            senderSocket.close();
        }
    }

    private class HandleWork extends Thread {
        // JDBC driver name and database URL
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String DB_URL = "jdbc:mysql://localhost/SDProject";

        //  Database credentials
        static final String USER = "root";
        static final String PASS = "password";

        // Needed objects
        private MulticastSocket socket = null;
        private DatagramPacket packet = null;
        private String udp;

        public HandleWork(DatagramPacket packet, MulticastSocket socket){
            this.packet = packet;
            this.socket = socket;
        }


        public void run() {
            UDP udp = new UDP();
            String received = new String(this.packet.getData(),0,this.packet.getLength());
            ArrayList<String> arr = udp.packetToArr(received);
            String sql = udp.menuToSQL(arr);
            System.out.println(arr);
            switch (arr.get(0).toLowerCase()){
                case"create":
                    insertDB(sql);
                    break;
                case"delete":
                    deleteDB(sql);
                    break;
                case"update":
                    updateDB(sql);
                    break;
                case"search":
                    selectDB(sql);
                    break;
            }



            String texto = "IT REACHED A WHOLE NEW LEVEL";
            // Send information from database to multicast
            byte [] b = texto.getBytes();
            DatagramPacket reply = new DatagramPacket(b, b.length, this.packet.getAddress(), RMI_PORT);
            try{
                this.socket.send(reply);
            }catch(IOException ie){
                System.out.println(ie);
            }
        }


        public void insertDB(String sql){
            Connection conn = null;
            Statement stmt = null;

            try{
                //STEP 2: Register JDBC driver
                Class.forName("com.mysql.jdbc.Driver");

                //STEP 3: Open a connection
                System.out.println("Connecting to a selected database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected database successfully...");

                System.out.println("Inserting records into the table...");
                stmt = conn.createStatement();

                stmt.executeUpdate(sql);

            }catch(SQLException se){
                se.printStackTrace();  //Handle errors for JDBC
            }catch(Exception e){
                e.printStackTrace(); //Handle errors for Class.forName
            }finally{
                //finally block used to close resources
                try{
                    if(stmt!=null)
                        conn.close();
                }catch(SQLException se){ }
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
        }

        public void updateDB(String sql){
            Connection conn = null;
            Statement stmt = null;

            try{
                //STEP 2: Register JDBC driver
                Class.forName("com.mysql.jdbc.Driver");

                //STEP 3: Open a connection
                System.out.println("Connecting to a selected database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected database successfully...");

                //STEP 4
                System.out.println("Creating statement...");
                stmt = conn.createStatement();

                stmt.executeUpdate(sql);

            }catch(SQLException se){
                System.out.println("USER ALREADY EXISTS");

            }catch(Exception e){
                e.printStackTrace(); //Handle errors for Class.forName
            }finally{
                //finally block used to close resources
                try{
                    if(stmt!=null)
                        conn.close();
                }catch(SQLException se){ }
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
        }

        public void deleteDB(String sql){
            Connection conn = null;
            Statement stmt = null;

            try{
                //STEP 2: Register JDBC driver
                Class.forName("com.mysql.jdbc.Driver");

                //STEP 3: Open a connection
                System.out.println("Connecting to a selected database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected database successfully...");

                //STEP 4:
                System.out.println("Creating statement...");
                stmt = conn.createStatement();

                stmt.executeUpdate(sql);

            }catch(SQLException se){
                se.printStackTrace();  //Handle errors for JDBC
            }catch(Exception e){
                e.printStackTrace(); //Handle errors for Class.forName
            }finally{
                //finally block used to close resources
                try{
                    if(stmt!=null)
                        conn.close();
                }catch(SQLException se){ }
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
        }

        public void selectDB(String sql){
            System.out.println(sql);
            Connection conn = null;
            Statement stmt = null;

            try{
                //STEP 2: Register JDBC driver
                Class.forName("com.mysql.jdbc.Driver");

                //STEP 3: Open a connection
                System.out.println("Connecting to a selected database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected database successfully...");
                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery(sql);

                //STEP 5: Extract data from result set
                while(rs.next()){
                    //Retrieve by column name
                    String user = rs.getString("username");
                    String email = rs.getString("email");
                    String name = rs.getString("name");
                    String personalinfo = rs.getString("personalinfo");

                    //Display values
                    System.out.print("Username: " + user);
                    System.out.print("email: " + email);
                    System.out.print("name: " + name);
                    System.out.println("PersonalInfo: " + personalinfo);
                }
                rs.close();

        }catch(SQLException se){
            se.printStackTrace();  //Handle errors for JDBC
        }catch(Exception e){
            e.printStackTrace(); //Handle errors for Class.forName
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){ }
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }
}
}
