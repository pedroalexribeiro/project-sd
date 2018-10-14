import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class Multicast {
    private String MULTICAST_ADDRESS = "224.1.1.1";
    private int PORT_MULTICAST = 4321; // port for sending to group
    private int PORT = 2500; // port for sending and receiving from rmi server

    public static void main(String[] args) {
        MulticastSocket socket = null;
        int numThreads = 0;
        try {
            socket = new MulticastSocket(PORT);
            while (true) {
                // waits for packet from rmi server
                byte[] buffer = new byte[1000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                // creates thread to handle all the work
                HandleWork thread = new HandleWork(packet, getAvailablePort(PORT, numThreads), MULTICAST_ADDRESS, PORT_MULTICAST);
                thread.start();
                numThreads++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public static int getAvailablePort(int numThreads) {
        return PORT + numThreads;
    }


}

public class HandleWork extends Thread {
    private DatagramSocket socket = null;
    private DatagramPacket packet = null;
    private int port_multicast = null;
    private String multicast_address = null;

    public HandleWork(DatagramPacket packet, int port, String multicast_address, int port_multicast){
        try{
            this.packet = packet;
            this.socket = new DatagramSocket(port);
            this.multicast_address = multicast_address;
            this.port_multicast = port_multicast;
            //super("Server " + id);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.socket.close();
        }
    }   

    public void run() {
        // Send it to multicast servers
        InetAddress group = InetAddress.getByName(this.multicast_address);
        DatagramPacket packet = new DatagramPacket(this.packet.getData(), this.packet.getLength(), group, this.port_multicast);
        this.socket.send(packet);
        // Waits for multicast servers to respond
        byte[] buffer = new byte[1000];             
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(request);
        // Sends information to rmi server
        DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), this.packet.getAddress(), this.packet.getPort());
        this.socket.send(reply);
        // closes socket and ends thread
        this.socket.close();
    }

}
