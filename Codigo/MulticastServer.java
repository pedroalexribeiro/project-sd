/*import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.sql.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class MulticastServer {
    private static String MULTICAST_ADDRESS = "224.1.1.1";
    private static int PORT = 4321;

    public static void main(String[] args) {
        // argumentos da linha de comando: id do server
        if(args.length == 0){
            System.out.println("id of server needs to be an argument");
            System.exit(0);
        }
        MulticastSocket socket = null;
        Server server = new Server(args[0]);
        int numThreads = 0;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                // waits for packet from multicast
                byte[] buffer = new byte[1000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                // creates thread to handle the work
                HandleWork thread = new HandleWork(packet, server.getAvailablePort(PORT, numThreads));
                thread.start();
                numThreads++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

public class Server {
    private int id=null;

    public Server(int id) {
        this.id = id;
    }

    public int getAvailablePort(int port, int counter) {
        return (this.id * port) + counter;

    }

}


public class HandleWork extends Thread {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/SDProject";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "password";

    private DatagramSocket socket = null;
    private DatagramPacket packet = null;
    private String udp;

    public HandleWork(DatagramPacket packet, int port){
        try{
            this.packet = packet;
            this.socket = new DatagramSocket(port);
            //super("Server " + id);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.socket.close();
        }
    }


    public void run() {
        // Splits string
        // Checks database
        // Creates string
        switch (udp){
            case "1":
                break;
            case "2":
                break;
            case "3":
                break;
                default:

                    break;
        }




        String texto = null;
        // Send information from database to multicast
        byte [] b = texto.getBytes();
        DatagramPacket reply = new DatagramPacket(b, b.length, packet.getAddress(), packet.getPort());
        try{
            this.socket.send(reply);

            // closes socket and finishes thread
            this.socket.close();

        }catch(IOException ie){
            System.out.println(ie);
        }
    }

    public void insertDB(String table,String info){
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

            String sql = "INSERT INTO" + table + "VALUES"+ info;
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

    public void updateDB(String table,String whatChanged,String where){
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

            String sql = "UPDATE" + table + "SET" + whatChanged + "WHERE" + where;
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

    public void deleteDB(String table,String where){
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

            String sql = "DELETE FROM" + table + " WHERE"+where;
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

    public void selectDB(String select,String table,String where){
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

            String sql = "SELECT"+ select +"FROM" + table + "WHERE" + where;
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            /*while(rs.next()){
                //Retrieve by column name
                int id  = rs.getInt("id");
                int age = rs.getInt("age");
                String first = rs.getString("first");
                String last = rs.getString("last");

                //Display values
                System.out.print("ID: " + id);
                System.out.print(", Age: " + age);
                System.out.print(", First: " + first);
                System.out.println(", Last: " + last);
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



String parts[] = t.split(" ");
        ArrayList<String> str = new ArrayList<>(Arrays.asList(parts));
*/