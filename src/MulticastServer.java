import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MulticastServer {
    private static String MULTICAST_ADDRESS = "224.1.1.1";
    private static int MULTICAST_PORT = 4100;
    private static int RMI_PORT = 4000;
    private static int TCP_PORT = 3900;
    private int id;
    private String ip;

    public static void main(String[] args) {
        // argumentos da linha de comando: id do server
        /*if(args.length == 0){
            System.out.println("id of server needs to be an argument");
            System.exit(0);
        }*/
        new MulticastServer();
    }

    public MulticastServer(){
        int randomNum = ThreadLocalRandom.current().nextInt(1, 200 + 1);
        this.id = randomNum;
        MulticastSocket receiveSocket = null;
        MulticastSocket senderSocket = null;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            this.ip = socket.getLocalAddress().getHostAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Starting...");
        ServerTCP tcp = new ServerTCP(TCP_PORT+this.id);
        tcp.start();
        try {
            receiveSocket = new MulticastSocket(MULTICAST_PORT);  // create socket and bind it
            senderSocket = new MulticastSocket();  // create socket and doesnt bind it cause only for sending
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            receiveSocket.joinGroup(group);
            System.out.println("I'm ready!");
            while (true) {
                // waits for packet from multicast
                byte[] buffer = new byte[1000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                receiveSocket.receive(packet);
                // creates thread to handle the work
                HandleWork thread = new HandleWork(packet, senderSocket, this.ip, this.id);
                thread.start();
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
        private String id;
        private String ip;

        public HandleWork(DatagramPacket packet, MulticastSocket socket, String ip, int id){
            this.packet = packet;
            this.socket = socket;
            this.ip = ip;
            this.id = Integer.toString(id);
        }


        public void run() {
            String received = new String(this.packet.getData(),0,this.packet.getLength());
            Map<String, String> hash = UDP.protocolToHash(received);
            String sql = UDP.menuToSQL(hash);
            String output = "";
            switch (hash.get("function")){
                case "broadcast":
                    output = this.id;
                    break;
                case"getIP":
                    output = this.ip + "|" + Integer.toString(TCP_PORT);
                    break;
                case "download":
                    SendFile thread = new SendFile(hash.get("ip"), Integer.parseInt(hash.get("port")), "Path is missing");
                    thread.start();
                    output = "Starting download...";
                    break;
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
                    if(this.id.equals(hash.get("serverID"))){
                        output = selectDB(sql,hash.get("what"));
                    }
                    break;
            }

            // Send information from database to multicast
            if(hash.get("function").equals("broadcast")){
                byte [] b = output.getBytes();
                DatagramPacket reply  = new DatagramPacket(b, b.length, this.packet.getAddress(), this.packet.getPort());
                try{
                    this.socket.send(reply);
                }catch(IOException ie){
                    System.out.println(ie);
                }
                return;
            }
            if(this.id.equals(hash.get("serverID"))){
                if(output==null || output.equals("")){
                    output="nothing";
                }
                output += ";messageID|" + Integer.toString(Integer.parseInt(hash.get("messageID"))+1);
                byte [] b = output.getBytes();
                DatagramPacket reply = new DatagramPacket(b, b.length, this.packet.getAddress(), RMI_PORT);
                try{
                    this.socket.send(reply);
                }catch(IOException ie){
                    System.out.println(ie);
                }
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
            Boolean foundSomething = false;
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
                    foundSomething = true;
                    try {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        // The column count starts from 1
                        for (int i = 1; i <= columnCount; i++) {
                            output += rsmd.getColumnName(i) + " | " + rs.getString(i) + " ; ";
                        }
                        output = output.substring(0,output.length()-2);
                        output += "**";

                    } catch (SQLException sqle) {
                        System.out.println(sqle);
                    }

                }
                if(foundSomething){
                    output = output.substring(0,output.length()-2);
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

    private class ServerTCP extends Thread {
        ServerSocket serverSck = null;

        public ServerTCP(int port){
            try{
                this.serverSck = new ServerSocket(port);
            }catch (IOException e){
                System.out.println(e);
            }
        }

        public void run(){
            if(serverSck.isBound()){
                while(true){
                    Socket client = null;
                    try {
                        client = this.serverSck.accept();
                        ReceiveFile thread = new ReceiveFile(client);
                        thread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class ReceiveFile extends Thread {
        Socket socket = null;

        public ReceiveFile(Socket socket){
            this.socket = socket;
        }

        public void run() {
            if (this.socket.isConnected()) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new BufferedInputStream(socket.getInputStream());
                    out = new FileOutputStream(new File("teste222.mp3"));
                    int read = 0;
                    byte[] buffer = new byte[1024];
                    while ((read = in.read(buffer)) != -1){
                        out.write(buffer, 0, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SendFile extends Thread {
        Socket socket = null;
        String path = null;

        public SendFile(String host, int port, String path) {
            try {
                this.socket = new Socket(host, port);
                this.path = path;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            File soundFile = new File(path);
            if (!soundFile.exists() || !soundFile.isFile()) {
                System.out.println("not a file: " + soundFile);
                return;
            }
            try {
                byte[] buffer = Files.readAllBytes(soundFile.toPath());
                OutputStream out = this.socket.getOutputStream();
                out.write(buffer, 0, buffer.length);
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
