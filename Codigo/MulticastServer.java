import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.sql.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
            String output = "";
            switch (arr.get(0).toLowerCase()){
                case"create":
                    output = insertDB(sql);
                    break;
                case"delete":
                    output = deleteDB(sql);
                    break;
                case"update":
                    output = updateDB(sql);
                    break;
                case"search":
                    output = selectDB(sql,arr.get(1));
                    break;
            }

            // Send information from database to multicast
            if(output==null){
                output="Nothing on Db";
            }

            byte [] b = output.getBytes();
            DatagramPacket reply = new DatagramPacket(b, b.length, this.packet.getAddress(), RMI_PORT);
            try{
                this.socket.send(reply);
            }catch(IOException ie){
                System.out.println(ie);
            }
        }


        private String insertDB(String sql){
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
                return "Success";
            }catch(SQLException se){
                return "Error";
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
            return "Error";
        }

        private String updateDB(String sql){
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
                return "Sucess";
            }catch(SQLException se){
                return "Error";
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
            return "Error";
        }

        private String deleteDB(String sql){
            Connection conn = null;
            Statement stmt = null;
            System.out.println(sql);
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

                return "Sucess";
            }catch(SQLException se){
                return "Error";
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
            return "Error";
        }

        private String selectDB(String sql,String type){
            Connection conn = null;
            Statement stmt = null;
            String output="";
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
                while(rs.next()) {
                    if(output.equals("")){
                        output = "Selected | " + type + " ; ";
                    }
                    try {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        // The column count starts from 1
                        for (int i = 1; i <= columnCount; i++) {
                            output += rsmd.getColumnName(i) + " | " + rs.getString(i) + " ; ";
                        }

                    } catch (SQLException sqle) {
                        System.out.println(sqle);
                    }

                }
                return output;

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
            return null;
        }
    }
}
