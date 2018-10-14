import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class MulticastServer {
    private String MULTICAST_ADDRESS = "224.1.1.1";
    private int PORT = 4321;

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
    private int id = null;

    public Server(int id) {
        this.id = id;
    }

    public getAvailablePort(int port, int counter) {
        return (this.id * port) + counter;
    }

}

public class HandleWork extends Thread {
    private DatagramSocket socket = null;
    private DatagramPacket packet = null;

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
        String texto = null;
        // Send information from database to multicast
        byte [] b = texto.getBytes();
        DatagramPacket reply = new DatagramPacket(b, b.length, packet.getAddress(), packet.getPort());
        this.socket.send(reply);
        // closes socket and finishes thread
        this.socket.close();
    }

}
